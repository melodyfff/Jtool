package com.xinchen.tool.perftest.sequenced;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestDisruptor;
import com.xinchen.tool.perftest.support.PerfTestUtil;
import com.xinchen.tool.perftest.support.ValueEventHandlerAddition;
import com.xinchen.tool.perftest.support.ValueEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.lmax.disruptor.RingBuffer.createSingleProducer;
import static com.xinchen.tool.perftest.support.PerfTestUtil.failIfNot;

/**
 *
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
 * @date 24/06/2020 15:50
 */
public class OneToOneSequencedThroughputTest extends AbstractPerfTestDisruptor {
    private static final int BUFFER_SIZE = 1024 * 64;
    private static final long ITERATIONS = 1000L * 1000L * 100L;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(DaemonThreadFactory.INSTANCE);
    private final long expectedResult = PerfTestUtil.accumulatedAddition(ITERATIONS);

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final RingBuffer<ValueEvent> ringBuffer = createSingleProducer(ValueEvent.EVENT_FACTORY, BUFFER_SIZE, new YieldingWaitStrategy());
    private final SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
    private final ValueEventHandlerAddition handler = new ValueEventHandlerAddition();
    private final BatchEventProcessor<ValueEvent> batchEventProcessor = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, handler);


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
        long expectedCount = batchEventProcessor.getSequence().get() + ITERATIONS;


        handler.reset(latch, expectedCount);
        executor.submit(batchEventProcessor);

        long start = System.currentTimeMillis();
        final RingBuffer<ValueEvent> rb = ringBuffer;

        for (long i = 0; i < ITERATIONS; i++) {
            // get next sequence
            long next = rb.next();
            // 设置事件
            rb.get(next).setValue(i);
            // 发布事件
            rb.publish(next);
        }

        latch.await();
        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);
        waitForEventProcessorSequence(expectedCount);
        batchEventProcessor.halt();

        failIfNot(expectedResult, handler.getValue());

        return opsPerSecond;
    }

    private void waitForEventProcessorSequence(long expectedCount) throws InterruptedException {
        while (batchEventProcessor.getSequence().get() != expectedCount) {
            Thread.sleep(1);
        }
    }

    public static void main(String[] args) throws Exception {
        OneToOneSequencedThroughputTest test = new OneToOneSequencedThroughputTest();
        test.testImplementations();
    }
}