package com.xinchen.tool.perftest.translator;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestDisruptor;
import com.xinchen.tool.perftest.support.PerfTestUtil;
import com.xinchen.tool.perftest.support.ValueEventHandlerAddition;
import com.xinchen.tool.perftest.support.ValueEvent;
import com.xinchen.tool.perftest.support.util.MutableLong;

import java.util.concurrent.CountDownLatch;

import static com.xinchen.tool.perftest.support.PerfTestUtil.failIfNot;

/**
 * <pre>
 * UniCast a series of items between 1 publisher and 1 event processor using the EventTranslator API
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
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/6/23 22:10
 */
public class OneToOneTranslatorThroughputTest extends AbstractPerfTestDisruptor {
    private static final int BUFFER_SIZE = 1024 * 64;
    private static final long ITERATIONS = 1000L * 1000L * 100L;
    private final long expectedResult = PerfTestUtil.accumulatedAddition(ITERATIONS);
    /** 模拟阶乘相加 ， 持有最终计算结果 */
    private final ValueEventHandlerAddition handler = new ValueEventHandlerAddition();
    private final RingBuffer<ValueEvent> ringBuffer;
    /** 这里的可变Lone主要是当作发布事件时的临时存储值 */
    private final MutableLong value = new MutableLong(0);

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    public OneToOneTranslatorThroughputTest() {
        // init disruptor
        // 这里使用的 Executor 为com.lmax.disruptor.dsl.BasicExecutor
        Disruptor<ValueEvent> disruptor =
                new Disruptor<>(
                        // eventFactory ,  the factory to create events in the ring buffer.
                        ValueEvent.EVENT_FACTORY,
                        // ringBufferSize, the size of the ring buffer, must be power of 2.
                        BUFFER_SIZE,
                        // a {@link ThreadFactory} to create threads for processors.
                        DaemonThreadFactory.INSTANCE,
                        // producerType , the claim strategy to use for the ring buffer.
                        ProducerType.SINGLE,
                        // waitStrategy, the wait strategy to use for the ring buffer.
                        new YieldingWaitStrategy());

        // Set up event handlers to handle events from the ring buffer
        // 这些handlers会并发生效
        // 可以作为chain启动，如： <pre><code>dw.handleEventsWith(A).then(B);</code></pre>
        disruptor.handleEventsWith(handler);

        // 这里使用start()获取ringBuffer是为了防止覆盖还未被最慢的event processor处理的输入 (确保生产者不会覆盖消费者未来得及处理的消息)
        // 当所有的event processors添加完成后，start()只能被调用一次，返回一个配置完成的ring buffer
        // 由一个AtomicBoolean started控制，当started为true时表示启动
        // disruptor内提供了一种存储机制(repository mechanism) consumerRepository ，将{@link EventHandler} 和 {@link EventProcessor} 关联
        this.ringBuffer = disruptor.start();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 2;
    }

    @Override
    protected long runDisruptorPass() throws InterruptedException {
        MutableLong value = this.value;

        final CountDownLatch latch = new CountDownLatch(1);
        long expectedCount = ringBuffer.getMinimumGatingSequence() + ITERATIONS;

        handler.reset(latch, expectedCount);
        long start = System.currentTimeMillis();

        final RingBuffer<ValueEvent> rb = ringBuffer;

        for (long l = 0; l < ITERATIONS; l++) {
            // 设置值
            value.set(l);

            // 发布事件时主要做了以下工作：
            // 1. sequencer.next()获取sequence
            // 2. translateAndPublish , 使用上面获取到的sequence号直接调用translator.translateTo(get(sequence), sequence, arg0)进行转换和对事件处理
            // 最后在finally块中由 sequencer.publish(sequence); 发布事件，这里主要是SingleProducerSequencer
            // SingleProducerSequencer.publish(long) 设置游标的sequence，调用waitStrategy通知消费者状态更新可以被消费
            rb.publishEvent(Translator.INSTANCE, value);
        }


        // 在ValueAdditionEventHandler中，当执行的总次数count等于预期sequence值时，latch.countDown()
        latch.await();

        // 计算 ops
        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);

        // 等待所有事件被处理完
        waitForEventProcessorSequence(expectedCount);

        // 判断结果是否符合预期
        failIfNot(expectedResult, handler.getValue());

        return opsPerSecond;
    }

    private static class Translator implements EventTranslatorOneArg<ValueEvent, MutableLong> {
        private static final Translator INSTANCE = new Translator();

        @Override
        public void translateTo(ValueEvent event, long sequence, MutableLong arg0) {
            event.setValue(arg0.get());
        }
    }

    private void waitForEventProcessorSequence(long expectedCount) throws InterruptedException {
        // The minimum gating sequence or the cursor sequence if no sequences have been added.
        while (ringBuffer.getMinimumGatingSequence() != expectedCount) {
            Thread.sleep(1);
        }
    }

    public static void main(String[] args) throws Exception {
        OneToOneTranslatorThroughputTest test = new OneToOneTranslatorThroughputTest();
        test.testImplementations();
    }
}