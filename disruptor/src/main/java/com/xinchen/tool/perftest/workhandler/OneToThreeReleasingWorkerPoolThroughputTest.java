package com.xinchen.tool.perftest.workhandler;

import com.lmax.disruptor.FatalExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestDisruptor;
import com.xinchen.tool.perftest.support.EventCountingAndReleasingWorkHandler;
import com.xinchen.tool.perftest.support.ValueEvent;
import com.xinchen.tool.perftest.support.util.PaddedLong;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.xinchen.tool.perftest.support.PerfTestUtil.failIfNot;

/**
 * @author xinchen
 * @version 1.0
 * @date 28/06/2020 15:41
 */
public final class OneToThreeReleasingWorkerPoolThroughputTest extends AbstractPerfTestDisruptor {
    private static final int NUM_WORKERS = 1;
    private static final int BUFFER_SIZE = 1024 * 8;
    private static final long ITERATIONS = 1000L * 1000 * 10L;
    private final ExecutorService executor = Executors.newFixedThreadPool(NUM_WORKERS, DaemonThreadFactory.INSTANCE);

    /** 统计执行总次数 */
    private final PaddedLong[] counters = new PaddedLong[NUM_WORKERS];

    {
        for (int i = 0; i < NUM_WORKERS; i++) {
            counters[i] = new PaddedLong();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final EventCountingAndReleasingWorkHandler[] handlers = new EventCountingAndReleasingWorkHandler[NUM_WORKERS];

    {
        for (int i = 0; i < NUM_WORKERS; i++) {

            handlers[i] = new EventCountingAndReleasingWorkHandler(counters, i);
        }
    }

    private final RingBuffer<ValueEvent> ringBuffer =
            RingBuffer.createSingleProducer(
                    ValueEvent.EVENT_FACTORY,
                    BUFFER_SIZE,
                    new YieldingWaitStrategy());

    private final WorkerPool<ValueEvent> workerPool =
            new WorkerPool<>(
                    ringBuffer,
                    ringBuffer.newBarrier(),
                    new FatalExceptionHandler(),
                    handlers);

    {
        // 这里需要特别注意
        // workerPool中有个属性workSequence在创建子WorkProcessor时都传入了相同的一个workSequence用于控制当前处理的进度
        // 而在子WorkProcessor中有自己的sequence区别于workSequence
        // 这里的addGatingSequences,实质上为WorkProcessor的sequence
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 4;
    }

    @Override
    protected long runDisruptorPass() throws InterruptedException {
        // 重置所有计数器为0
        resetCounters();

        // 指定workerPool运行的线程池，并开始启动,主要做以下几件事情
        // 1. 获取ringBuffer的cursor位置，并设置为workerPool中的workSequence的值
        // 2. 分别设置workerpool中的workProcessors的sequence值为cursor值， 同时提交到线程池进行监听启动等待消费
        RingBuffer<ValueEvent> ringBuffer = workerPool.start(executor);



        long start = System.currentTimeMillis();

        // 总共发布ITERATIONS次事件，等待被WorkerPool中的handler处理
        for (long i = 0; i < ITERATIONS; i++) {
            long sequence = ringBuffer.next();
            ringBuffer.get(sequence).setValue(i);
            ringBuffer.publish(sequence);
        }

        workerPool.drainAndHalt();

        // Workaround to ensure that the last worker(s) have completed after releasing their events
        Thread.sleep(1L);

        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);

        // 通过判断总的执行次数来确定是否符合预期
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
        new OneToThreeReleasingWorkerPoolThroughputTest().testImplementations();
    }
}