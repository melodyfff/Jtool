package com.xinchen.tool.perftest.sequenced;

import static com.lmax.disruptor.RingBuffer.createSingleProducer;
import static com.xinchen.tool.perftest.support.PerfTestUtil.failIfNot;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestDisruptor;
import com.xinchen.tool.perftest.support.PerfTestUtil;
import com.xinchen.tool.perftest.support.ValueEventHandlerAddition;
import com.xinchen.tool.perftest.support.ValueEvent;

/**
 * 一对一顺序批处理
 *
 * 模拟结果计算
 * <pre>
 *     预期结果： (0 + 1 + 2 + 3 ... + ITERATIONS - 1) * BATCH_SIZE
 *
 *     生成包含long值的{@link ValueEvent},通过ringBuffer.publish()产生，由BatchEventProcessor<ValueEvent>事件处理进程去进行处理
 *     其中{@link ValueEventHandlerAddition}处理{@link ValueEvent}事件(accumulated Addition)，控制处理次数和进度，并持有最终计算结果
 *
 * </pre>
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
 * @date 23/06/2020 10:41
 */
public final class OneToOneSequencedBatchThroughputTest extends AbstractPerfTestDisruptor {
    /** BATCH SIZE */
    public static final int BATCH_SIZE = 10;

    /** RingBuffer Size 在环形缓冲区中创建的元素数*/
    private static final int BUFFER_SIZE = 1024 * 64;

    /** Times */
    private static final long ITERATIONS = 1000L * 1000L * 100L;

    /** Single Thread All threads are created with setDaemon(true). */
    private final ExecutorService executor = Executors.newSingleThreadExecutor(DaemonThreadFactory.INSTANCE);

    /** 期待的结果值，(0 + 1 + 2 + 3 ... + ITERATIONS - 1) * BATCH_SIZE  */
    private final long expectedResult = PerfTestUtil.accumulatedAddition(ITERATIONS) * BATCH_SIZE;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** Create a new single producer RingBuffer with the specified wait strategy.
        Yielding strategy that uses a Thread.yield() */
    private final RingBuffer<ValueEvent> ringBuffer = createSingleProducer(ValueEvent.EVENT_FACTORY, BUFFER_SIZE, new YieldingWaitStrategy());
    private final SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
    private final ValueEventHandlerAddition handler = new ValueEventHandlerAddition();
    private final BatchEventProcessor<ValueEvent> batchEventProcessor = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, handler);


    {
        // Add the specified gating sequences to this instance of the Disruptor.
        // safely and atomically added to the list of gating sequences.
        // 设置门阀序列, 事件消费者序列维护到gating sequence
        ringBuffer.addGatingSequences(batchEventProcessor.getSequence());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 2;
    }

    /**
     * batchEventProcessor 消费消息： {@link com.lmax.disruptor.BatchEventProcessor.processEvents()}
     *
     * @return opsPerSecond 每秒处理的任务数
     * @throws InterruptedException InterruptedException
     */
    @Override
    protected long runDisruptorPass() throws InterruptedException {
        // 门闩
        final CountDownLatch latch = new CountDownLatch(1);

        // 期待执行的次数
        // 预先计算游标将要达到的位置位置，计算门阀
        long expectedCount = batchEventProcessor.getSequence().get() + ITERATIONS * BATCH_SIZE;

        // 重设值
        handler.reset(latch, expectedCount);

        // 提交任务执行,消费者开始监听
        executor.submit(batchEventProcessor);

        long start = System.currentTimeMillis();
        final RingBuffer<ValueEvent> rb = ringBuffer;
        for (long i = 0; i < ITERATIONS; i++) {
            // 这里真正执行的是Sequencer  com.lmax.disruptor.SingleProducerSequencer.next(int)
            long high = rb.next(BATCH_SIZE);
            long low = high - (BATCH_SIZE - 1);
            for (long l = low; l <= high; l++) {
                // 从给定sequence中获取事件,并设置事件源(每次生成新的event)
                rb.get(l).setValue(i);
            }

            // 发布指定sequences，标记这些sequences可被消费 ,这里其实主要是推送BATCH_SIZE数量的事件
            // 这里主要是com.lmax.disruptor.SingleProducerSequencer.publish(long)
            // 1. cursor.set(sequence); 设置游标的值
            // 2. waitStrategy.signalAllWhenBlocking(); 向所有还在等待的{@link EventProcessor} 发送信号，通知cursor已经前进
            rb.publish(low, high);
        }

        // 等待队列处理完成
        latch.await();

        // 计算每秒处理能力
        long opsPerSecond = (BATCH_SIZE * ITERATIONS * 1000L) / (System.currentTimeMillis() - start);

        // 这里主要是等待batchEventProcessor完全执行完成
        // sequence :  tracking the progress of the ring buffer and event processors
        waitForEventProcessorSequence(expectedCount);
        // 停止事件处理进程
        // 1. running.set(HALTED); 修改状态为 HALTED
        // 2. sequenceBarrier.alert(); 通知线程检查状态，消费完后停止
        batchEventProcessor.halt();

        // 判断handler中处理的最终结果是否和预期结果一致
        failIfNot(expectedResult, handler.getValue());

        // operate per second
        return opsPerSecond;
    }

    private void waitForEventProcessorSequence(long expectedCount) throws InterruptedException {
        while (batchEventProcessor.getSequence().get() != expectedCount) {
            Thread.sleep(1);
        }
    }

    public static void main(String[] args) throws Exception {
        OneToOneSequencedBatchThroughputTest test = new OneToOneSequencedBatchThroughputTest();
        test.testImplementations();
    }
}