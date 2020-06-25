package com.xinchen.tool.perftest.sequenced;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestDisruptor;
import com.xinchen.tool.perftest.support.ValueEventHandlerAddition;
import com.xinchen.tool.perftest.support.ValueEvent;
import com.xinchen.tool.perftest.support.ValuePublisher;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.lmax.disruptor.RingBuffer.createMultiProducer;

/**
 *
 * <pre>
 *
 * Sequence a series of events from multiple publishers going to one event processor.
 *
 * +----+
 * | P1 |------+
 * +----+      |
 *             v
 * +----+    +-----+
 * | P1 |--->| EP1 |
 * +----+    +-----+
 *             ^
 * +----+      |
 * | P3 |------+
 * +----+
 *
 *
 * Disruptor:
 * ==========
 *             track to prevent wrap
 *             +--------------------+
 *             |                    |
 *             |                    v
 * +----+    +====+    +====+    +-----+
 * | P1 |--->| RB |<---| SB |    | EP1 |
 * +----+    +====+    +====+    +-----+
 *             ^   get    ^         |
 * +----+      |          |         |
 * | P2 |------+          +---------+
 * +----+      |            waitFor
 *             |
 * +----+      |
 * | P3 |------+
 * +----+
 *
 * P1  - Publisher 1
 * P2  - Publisher 2
 * P3  - Publisher 3
 * RB  - RingBuffer
 * SB  - SequenceBarrier
 * EP1 - EventProcessor 1
 *
 * </pre>
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/6/25 22:35
 */
public final class ThreeToOneSequencedThroughputTest extends AbstractPerfTestDisruptor {
    private static final int NUM_PUBLISHERS = 3;
    private static final int BUFFER_SIZE = 1024 * 64;
    private static final long ITERATIONS = 1000L * 1000L * 20L;
    private final ExecutorService executor = Executors.newFixedThreadPool(NUM_PUBLISHERS + 1, DaemonThreadFactory.INSTANCE);
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(NUM_PUBLISHERS + 1);

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final RingBuffer<ValueEvent> ringBuffer = createMultiProducer(ValueEvent.EVENT_FACTORY, BUFFER_SIZE, new BusySpinWaitStrategy());

    private final SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
    private final ValueEventHandlerAddition handler = new ValueEventHandlerAddition();
    private final BatchEventProcessor<ValueEvent> batchEventProcessor = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, handler);
    private final ValuePublisher[] valuePublishers = new ValuePublisher[NUM_PUBLISHERS];

    {
        for (int i = 0; i < NUM_PUBLISHERS; i++) {
            // 创建publisher，由cyclicBarrier控制同时开始publish,此处由3个publish平分ITERATIONS个事件
            valuePublishers[i] = new ValuePublisher(cyclicBarrier, ringBuffer, ITERATIONS / NUM_PUBLISHERS);
        }

        ringBuffer.addGatingSequences(batchEventProcessor.getSequence());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 4;
    }

    @Override
    protected long runDisruptorPass() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        handler
                .reset(latch, batchEventProcessor.getSequence().get() + ((ITERATIONS / NUM_PUBLISHERS) * NUM_PUBLISHERS));

        Future<?>[] futures = new Future[NUM_PUBLISHERS];
        for (int i = 0; i < NUM_PUBLISHERS; i++) {
            // 提交生产者线程
            futures[i] = executor.submit(valuePublishers[i]);
        }

        // 提交消费者线程
        executor.submit(batchEventProcessor);

        long start = System.currentTimeMillis();
        // 生产者同时开始生产
        cyclicBarrier.await();

        for (int i = 0; i < NUM_PUBLISHERS; i++) {
            futures[i].get();
        }

        latch.await();

        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);
        batchEventProcessor.halt();

        return opsPerSecond;
    }

    public static void main(String[] args) throws Exception {
        new ThreeToOneSequencedThroughputTest().testImplementations();
    }
}
