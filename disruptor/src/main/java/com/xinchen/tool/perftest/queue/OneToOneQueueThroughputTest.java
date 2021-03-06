package com.xinchen.tool.perftest.queue;

import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestQueue;
import com.xinchen.tool.perftest.support.ValueQueueProcessorAddition;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import static com.xinchen.tool.perftest.support.PerfTestUtil.failIfNot;

/**
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
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/6/27 21:38
 */
public final class OneToOneQueueThroughputTest extends AbstractPerfTestQueue {
    private static final int BUFFER_SIZE = 1024 * 64;
    private static final long ITERATIONS = 1000L * 1000L * 10L;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(DaemonThreadFactory.INSTANCE);
    /** 结果预期，ValueAdditionQueueProcessor中持有结果，并不断对队列中的值进行累加 */
    private final long expectedResult = ITERATIONS * 3L;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final BlockingQueue<Long> blockingQueue = new LinkedBlockingQueue<>(BUFFER_SIZE);
    private final ValueQueueProcessorAddition queueProcessor = new ValueQueueProcessorAddition(blockingQueue, ITERATIONS);

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 2;
    }

    @Override
    protected long runQueuePass() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        queueProcessor.reset(latch);

        // 提交queue消费者
        Future<?> future = executor.submit(queueProcessor);
        long start = System.currentTimeMillis();

        // 不断往队列中设值
        for (long i = 0; i < ITERATIONS; i++) {
            blockingQueue.put(3L);
        }

        latch.await();
        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);

        queueProcessor.halt();
        future.cancel(true);


        // 获取结果
        final long value = queueProcessor.getValue();
        failIfNot(expectedResult, value);

        return opsPerSecond;
    }

    public static void main(String[] args) throws Exception {
        OneToOneQueueThroughputTest test = new OneToOneQueueThroughputTest();
        test.testImplementations();
    }
}

