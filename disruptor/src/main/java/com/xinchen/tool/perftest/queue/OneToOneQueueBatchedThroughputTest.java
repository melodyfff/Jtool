package com.xinchen.tool.perftest.queue;

import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestQueue;
import com.xinchen.tool.perftest.support.ValueQueueProcessorAdditionBatch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import static com.xinchen.tool.perftest.support.PerfTestUtil.failIfNot;

/**
 *
 * <pre>
 * UniCast a series of items between 1 publisher and 1 event processor.
 *
 * +----+    +-----+
 * | P1 |--->| EP1 |
 * +----+    +-----+
 *
 * Queue Based:
 * ============
 *
 *        put      take
 * +----+    +====+    +-----+
 * | P1 |--->| Q1 |<---| EP1 |
 * +----+    +====+    +-----+
 *
 * P1  - Publisher 1
 * Q1  - Queue 1
 * EP1 - EventProcessor 1
 *
 * </pre>
 *
 * @author xinchen
 * @version 1.0
 * @date 28/06/2020 12:56
 */
public final class OneToOneQueueBatchedThroughputTest extends AbstractPerfTestQueue {
    private static final int BUFFER_SIZE = 1024 * 64;
    private static final long ITERATIONS = 1000L * 1000L * 10L;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(DaemonThreadFactory.INSTANCE);
    private final long expectedResult = ITERATIONS * 3L;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final BlockingQueue<Long> blockingQueue = new LinkedBlockingQueue<Long>(BUFFER_SIZE);
    private final ValueQueueProcessorAdditionBatch queueProcessor = new ValueQueueProcessorAdditionBatch(blockingQueue, ITERATIONS);

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 2;
    }

    @Override
    protected long runQueuePass() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        queueProcessor.reset(latch);
        Future<?> future = executor.submit(queueProcessor);
        long start = System.currentTimeMillis();

        for (long i = 0; i < ITERATIONS; i++) {
            blockingQueue.put(3L);
        }

        latch.await();
        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);
        queueProcessor.halt();
        future.cancel(true);

        failIfNot(expectedResult, queueProcessor.getValue());

        return opsPerSecond;
    }

    public static void main(String[] args) throws Exception {
        OneToOneQueueBatchedThroughputTest test = new OneToOneQueueBatchedThroughputTest();
        test.testImplementations();
    }
}
