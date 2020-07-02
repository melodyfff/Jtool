package com.xinchen.tool.perftest.raw;

import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.Sequenced;
import com.lmax.disruptor.Sequencer;
import com.lmax.disruptor.SingleProducerSequencer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestDisruptor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <pre>
 * UniCast a series of items between 1 publisher and 1 event processor.
 *
 * +----+    +-----+
 * | P1 |--->| EP1 |
 * +----+    +-----+
 *
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
 * @date 02/07/2020 08:52
 */
public class OneToOneRawThroughputTest extends AbstractPerfTestDisruptor {
    private static final int BUFFER_SIZE = 1024 * 64;
    private static final long ITERATIONS = 1000L * 1000L * 200L;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(DaemonThreadFactory.INSTANCE);

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final Sequencer sequencer = new SingleProducerSequencer(BUFFER_SIZE, new YieldingWaitStrategy());
    private final MyRunnable myRunnable = new MyRunnable(sequencer);

    {
        // 添加门阀
        sequencer.addGatingSequences(myRunnable.sequence);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 2;
    }

    @Override
    protected long runDisruptorPass() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        // 估算执行次数
        long expectedCount = myRunnable.sequence.get() + ITERATIONS;

        myRunnable.reset(latch, expectedCount);

        executor.submit(myRunnable);

        long start = System.currentTimeMillis();

        final Sequenced sequencer = this.sequencer;


        // 生产者生产,这里无实际event，只是通知消费者该sequence可读
        for (long i = 0; i < ITERATIONS; i++) {
            long next = sequencer.next();
            sequencer.publish(next);
        }

        // 等待消费者读取完event
        latch.await();

        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);

        // 等待处理得慢的线程处理完成
        waitForEventProcessorSequence(expectedCount);

        return opsPerSecond;
    }

    private void waitForEventProcessorSequence(long expectedCount) throws InterruptedException {
        while (myRunnable.sequence.get() != expectedCount) {
            Thread.sleep(1);
        }
    }

    private static class MyRunnable implements Runnable {
        private CountDownLatch latch;
        private long expectedCount;
        Sequence sequence = new Sequence(-1);
        private final SequenceBarrier barrier;

        MyRunnable(Sequencer sequencer) {
            this.barrier = sequencer.newBarrier();
        }

        public void reset(CountDownLatch latch, long expectedCount) {
            this.latch = latch;
            this.expectedCount = expectedCount;
        }

        @Override
        public void run() {
            long expected = expectedCount;
            long processed = -1;


            try {
                do {
                    // 无实际处理，滚动自身维护的sequence
                    // 模拟已经处理完事件，开始获取下一事件的sequence
                    processed = barrier.waitFor(sequence.get() + 1);
                    // 更新自己的sequence，已经处理完的序列位置
                    sequence.set(processed);
                }
                while (processed < expected);

                latch.countDown();
                sequence.setVolatile(processed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        OneToOneRawThroughputTest test = new OneToOneRawThroughputTest();
        test.testImplementations();
    }
}

