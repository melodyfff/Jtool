package com.xinchen.tool.perftest.sequenced;

import com.lmax.disruptor.EventPoller;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestDisruptor;
import com.xinchen.tool.perftest.support.PerfTestUtil;
import com.xinchen.tool.perftest.support.ValueEvent;
import com.xinchen.tool.perftest.support.util.PaddedLong;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.xinchen.tool.perftest.support.PerfTestUtil.failIfNot;

/**
 *
 * 事件轮询器，单个线程内，不断轮询处理事件
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
 * @date 24/06/2020 11:37
 */
public class OneToOneSequencedPollerThroughputTest extends AbstractPerfTestDisruptor {
    private static final int BUFFER_SIZE = 1024 * 64;
    private static final long ITERATIONS = 1000L * 1000L * 100L;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(DaemonThreadFactory.INSTANCE);
    private final long expectedResult = PerfTestUtil.accumulatedAddition(ITERATIONS);

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final RingBuffer<ValueEvent> ringBuffer = RingBuffer.createSingleProducer(ValueEvent.EVENT_FACTORY, BUFFER_SIZE, new YieldingWaitStrategy());
    /** 事件轮询器 */
    private final EventPoller<ValueEvent> poller = ringBuffer.newPoller();
    private final PollRunnable pollRunnable = new PollRunnable(poller);

    {
        ringBuffer.addGatingSequences(poller.getSequence());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 2;
    }

    private static class PollRunnable implements Runnable, EventPoller.Handler<ValueEvent> {
        // 轮询器
        private final EventPoller<ValueEvent> poller;
        // 控制轮询器的运行状态
        private volatile boolean running = true;
        // 持有运算结果
        private final PaddedLong value = new PaddedLong();
        private CountDownLatch latch;
        private long count;

        PollRunnable(EventPoller<ValueEvent> poller) {
            this.poller = poller;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    // 注： poller.poll(this)
                    // 这里通过线程不断去轮训来消费序列中的数据
                    if (EventPoller.PollState.PROCESSING != poller.poll(this)) {
                        // 如果当前poller没有处于PROCESSING状态，让出cpu使用
                        Thread.yield();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onEvent(ValueEvent event, long sequence, boolean endOfBatch) {
            // 模拟累加
            value.set(value.get() + event.getValue());

            if (count == sequence) {
                latch.countDown();
            }

            return true;
        }

        void halt() {
            // 停止线程
            running = false;
        }

        void reset(final CountDownLatch latch, final long expectedCount) {
            value.set(0L);
            this.latch = latch;
            count = expectedCount;
            running = true;
        }

        long getValue() {
            return value.get();
        }
    }

    @Override
    protected long runDisruptorPass() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        // 期待运行的次数
        long expectedCount = poller.getSequence().get() + ITERATIONS;

        // 重置初始值，并提交运行
        pollRunnable.reset(latch, expectedCount);
        executor.submit(pollRunnable);

        long start = System.currentTimeMillis();

        final RingBuffer<ValueEvent> rb = ringBuffer;

        for (long i = 0; i < ITERATIONS; i++) {
            long next = rb.next();
            rb.get(next).setValue(i);
            rb.publish(next);
        }

        latch.await();
        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);
        // 等待所有事件处理器执行完毕
        waitForEventProcessorSequence(expectedCount);
        // 停止轮询器线程
        pollRunnable.halt();

        failIfNot(expectedResult, pollRunnable.getValue());

        return opsPerSecond;
    }

    private void waitForEventProcessorSequence(long expectedCount) throws InterruptedException {
        while (poller.getSequence().get() != expectedCount) {
            Thread.sleep(1);
        }
    }

    public static void main(String[] args) throws Exception {
        OneToOneSequencedPollerThroughputTest test = new OneToOneSequencedPollerThroughputTest();
        test.testImplementations();
    }
}
