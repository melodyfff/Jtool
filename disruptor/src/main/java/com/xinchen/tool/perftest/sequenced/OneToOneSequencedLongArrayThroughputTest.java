package com.xinchen.tool.perftest.sequenced;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestDisruptor;
import com.xinchen.tool.perftest.support.LongArrayEventHandler;
import com.xinchen.tool.perftest.support.PerfTestUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 实例处理event[],这里的{@link LongArrayEventHandler}主要是将long[]中所有的值进行累加，并保存最终结果
 *
 * <pre>
 * UniCast a series of items between 1 publisher and 1 event processor.
 *
 * +----+    +-----+
 * | P1 |--->| EP1 |
 * +----+    +-----+
 *
 * Disruptor:
 * ==========
 *              track to prevent wrap
 *              +------------------+
 *              |                  |
 *              |                  v
 * +----+    +====+    +====+   +-----+
 * | P1 |--->| RB |<---| SB |   | EP1 |
 * +----+    +====+    +====+   +-----+
 *      claim      get    ^        |
 *                        |        |
 *                        +--------+
 *                          waitFor
 *
 * P1  - Publisher 1
 * RB  - RingBuffer
 * SB  - SequenceBarrier
 * EP1 - EventProcessor 1
 *
 * </pre>
 *
 * @author xinchen
 * @version 1.0
 * @date 23/06/2020 16:42
 */
public class OneToOneSequencedLongArrayThroughputTest extends AbstractPerfTestDisruptor {
    private static final int BUFFER_SIZE = 1024 * 1;
    private static final long ITERATIONS = 1000L * 1000L * 1L;
    private static final int ARRAY_SIZE = 2 * 1024;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(DaemonThreadFactory.INSTANCE);
    private static final EventFactory<long[]> FACTORY = new EventFactory<long[]>() {
        @Override
        public long[] newInstance() {
            return new long[ARRAY_SIZE];
        }
    };
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final RingBuffer<long[]> ringBuffer = RingBuffer.createSingleProducer(FACTORY, BUFFER_SIZE, new YieldingWaitStrategy());
    private final SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
    private final LongArrayEventHandler handler = new LongArrayEventHandler();
    private final BatchEventProcessor<long[]> batchEventProcessor = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, handler);

    {
        ringBuffer.addGatingSequences(batchEventProcessor.getSequence());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 2;
    }

    @Override
    protected long runDisruptorPass() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        // 估算会进行多少次事件处理
        long expectedCount = batchEventProcessor.getSequence().get() + ITERATIONS;

        handler.reset(latch, ITERATIONS);
        executor.submit(batchEventProcessor);

        long start = System.currentTimeMillis();
        final RingBuffer<long[]> rb = ringBuffer;

        for (long i = 0; i < ITERATIONS; i++) {
            // get nextSequence
            long next = rb.next();

            // 这里event[] 的大小为上列ARRAY_SIZE设置
            long[] event = rb.get(next);

            // 向event[]中填充值
            for (int j = 0; j < event.length; j++) {
                event[j] = i;
            }

            // 发布事件等待EventProcessor处理
            rb.publish(next);
        }

        latch.await();
        long opsPerSecond = (ITERATIONS * ARRAY_SIZE * 1000L) / (System.currentTimeMillis() - start);
        waitForEventProcessorSequence(expectedCount);
        batchEventProcessor.halt();

        // 估算所有long[]相加的结果进行判断
        PerfTestUtil.failIf(0, handler.getValue());
        PerfTestUtil.failIfNot(1023998976000000L, handler.getValue());

        return opsPerSecond;
    }

    private void waitForEventProcessorSequence(long expectedCount) throws InterruptedException {
        while (batchEventProcessor.getSequence().get() != expectedCount) {
            Thread.sleep(1);
        }
    }

    public static void main(String[] args) throws Exception {
        OneToOneSequencedLongArrayThroughputTest test = new OneToOneSequencedLongArrayThroughputTest();
        test.testImplementations();
    }
}

