package com.xinchen.tool.perftest.sequenced;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestDisruptor;
import com.xinchen.tool.perftest.support.Operation;
import com.xinchen.tool.perftest.support.ValueEvent;
import com.xinchen.tool.perftest.support.ValueEventHandlerMutation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.lmax.disruptor.RingBuffer.createSingleProducer;
import static com.xinchen.tool.perftest.support.PerfTestUtil.failIfNot;

/**
 *
 * <pre>
 *
 * MultiCast a series of items between 1 publisher and 3 event processors.
 *
 *           +-----+
 *    +----->| EP1 |
 *    |      +-----+
 *    |
 * +----+    +-----+
 * | P1 |--->| EP2 |
 * +----+    +-----+
 *    |
 *    |      +-----+
 *    +----->| EP3 |
 *           +-----+
 *
 * Disruptor:
 * ==========
 *                             track to prevent wrap
 *             +--------------------+----------+----------+
 *             |                    |          |          |
 *             |                    v          v          v
 * +----+    +====+    +====+    +-----+    +-----+    +-----+
 * | P1 |--->| RB |<---| SB |    | EP1 |    | EP2 |    | EP3 |
 * +----+    +====+    +====+    +-----+    +-----+    +-----+
 *      claim      get    ^         |          |          |
 *                        |         |          |          |
 *                        +---------+----------+----------+
 *                                      waitFor
 *
 * P1  - Publisher 1
 * RB  - RingBuffer
 * SB  - SequenceBarrier
 * EP1 - EventProcessor 1
 * EP2 - EventProcessor 2
 * EP3 - EventProcessor 3
 *
 * </pre>
 *
 * @author xinchen
 * @version 1.0
 * @date 24/06/2020 16:01
 */
public class OneToThreeSequencedThroughputTest extends AbstractPerfTestDisruptor {
    private static final int NUM_EVENT_PROCESSORS = 3;
    private static final int BUFFER_SIZE = 1024 * 8;
    private static final long ITERATIONS = 1000L * 1000L * 100L;
    private final ExecutorService executor = Executors.newFixedThreadPool(NUM_EVENT_PROCESSORS, DaemonThreadFactory.INSTANCE);
    /** 预期估算结果 */
    private final long[] results = new long[NUM_EVENT_PROCESSORS];

    {
        for (long i = 0; i < ITERATIONS; i++) {
            // 三个不同操作的消费者操作，计算最终结果值
            results[0] = Operation.ADDITION.op(results[0], i);
            results[1] = Operation.SUBTRACTION.op(results[1], i);
            results[2] = Operation.AND.op(results[2], i);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final RingBuffer<ValueEvent> ringBuffer = createSingleProducer(ValueEvent.EVENT_FACTORY, BUFFER_SIZE, new YieldingWaitStrategy());
    private final SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
    private final ValueEventHandlerMutation[] handlers = new ValueEventHandlerMutation[NUM_EVENT_PROCESSORS];

    {
        // 创建3个事件处理(3个消费者)
        handlers[0] = new ValueEventHandlerMutation(Operation.ADDITION);
        handlers[1] = new ValueEventHandlerMutation(Operation.SUBTRACTION);
        handlers[2] = new ValueEventHandlerMutation(Operation.AND);
    }

    /** 批处理 Event Processor */
    private final BatchEventProcessor<?>[] batchEventProcessors = new BatchEventProcessor[NUM_EVENT_PROCESSORS];

    {
        // 三个消费者
        batchEventProcessors[0] = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, handlers[0]);
        batchEventProcessors[1] = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, handlers[1]);
        batchEventProcessors[2] = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, handlers[2]);

        ringBuffer.addGatingSequences(
                batchEventProcessors[0].getSequence(),
                batchEventProcessors[1].getSequence(),
                batchEventProcessors[2].getSequence());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 4;
    }

    @Override
    protected long runDisruptorPass() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(NUM_EVENT_PROCESSORS);
        // reset handler and submit handler
        for (int i = 0; i < NUM_EVENT_PROCESSORS; i++) {
            handlers[i].reset(latch, batchEventProcessors[i].getSequence().get() + ITERATIONS);
            executor.submit(batchEventProcessors[i]);
        }

        long start = System.currentTimeMillis();

        for (long i = 0; i < ITERATIONS; i++) {
            // get next sequence
            long sequence = ringBuffer.next();
            // get event
            ringBuffer.get(sequence).setValue(i);
            // publish event
            // sequencer publish , 设置sequence状态为可用,根据不同的等待策略去唤醒处理阻塞的线程
            ringBuffer.publish(sequence);
        }

        latch.await();

        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);

        // stop event process
        for (int i = 0; i < NUM_EVENT_PROCESSORS; i++) {
            batchEventProcessors[i].halt();
            failIfNot(results[i], handlers[i].getValue());
        }

        return opsPerSecond;
    }

    public static void main(String[] args) throws Exception {
        new OneToThreeSequencedThroughputTest().testImplementations();
    }
}
