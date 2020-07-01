package com.xinchen.tool.perftest.workhandler;

import com.lmax.disruptor.FatalExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestDisruptor;
import com.xinchen.tool.perftest.support.EventCountingQueueProcessor;
import com.xinchen.tool.perftest.support.EventCountingWorkHandler;
import com.xinchen.tool.perftest.support.ValueEvent;
import com.xinchen.tool.perftest.support.util.PaddedLong;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static com.xinchen.tool.perftest.support.PerfTestUtil.failIfNot;

/**
 *
 *
 * {@link BlockingQueue}实现
 * 与{@link OneToThreeReleasingWorkerPoolThroughputTest}形成对比
 *
 * @author xinchen
 * @version 1.0
 * @date 01/07/2020 14:14
 */
public class OneToThreeWorkerPoolThroughputTest extends AbstractPerfTestDisruptor {
    private static final int NUM_WORKERS = 3;
    private static final int BUFFER_SIZE = 1024 * 8;
    private static final long ITERATIONS = 1000L * 1000L * 100L;
    private final ExecutorService executor = Executors.newFixedThreadPool(NUM_WORKERS, DaemonThreadFactory.INSTANCE);

    private final PaddedLong[] counters = new PaddedLong[NUM_WORKERS];

    {
        for (int i = 0; i < NUM_WORKERS; i++) {
            counters[i] = new PaddedLong();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    // 这里定义了 queueWorkers但是并没有真正使用到，所以感觉是想和workerPool进行对照

    private final BlockingQueue<Long> blockingQueue = new LinkedBlockingQueue<Long>(BUFFER_SIZE);
    private final EventCountingQueueProcessor[] queueWorkers = new EventCountingQueueProcessor[NUM_WORKERS];

    {
        for (int i = 0; i < NUM_WORKERS; i++) {
            queueWorkers[i] = new EventCountingQueueProcessor(blockingQueue, counters, i);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final EventCountingWorkHandler[] handlers = new EventCountingWorkHandler[NUM_WORKERS];

    {
        for (int i = 0; i < NUM_WORKERS; i++) {
            handlers[i] = new EventCountingWorkHandler(counters, i);
        }
    }

    private final RingBuffer<ValueEvent> ringBuffer =
            RingBuffer.createSingleProducer(
                    ValueEvent.EVENT_FACTORY,
                    BUFFER_SIZE,
                    new YieldingWaitStrategy());

    private final WorkerPool<ValueEvent> workerPool = new WorkerPool<>(
                    ringBuffer,
                    ringBuffer.newBarrier(),
                    new FatalExceptionHandler(),
                    handlers);

    {
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 4;
    }

    @Override
    protected long runDisruptorPass() throws InterruptedException {

        resetCounters();

        RingBuffer<ValueEvent> ringBuffer = workerPool.start(executor);
        long start = System.currentTimeMillis();

        for (long i = 0; i < ITERATIONS; i++) {
            long sequence = ringBuffer.next();
            ringBuffer.get(sequence).setValue(i);
            ringBuffer.publish(sequence);
        }


        workerPool.drainAndHalt();

        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);

        failIfNot(ITERATIONS, sumCounters());

        return opsPerSecond;
    }

    private void resetCounters() {
        for (int i = 0; i < NUM_WORKERS; i++) {
            counters[i].set(0L);
        }
    }

    private long sumCounters() {
        long sumJobs = 0L;
        for (int i = 0; i < NUM_WORKERS; i++) {
            sumJobs += counters[i].get();
        }

        return sumJobs;
    }

    public static void main(String[] args) throws Exception {
        new OneToThreeWorkerPoolThroughputTest().testImplementations();
    }
}