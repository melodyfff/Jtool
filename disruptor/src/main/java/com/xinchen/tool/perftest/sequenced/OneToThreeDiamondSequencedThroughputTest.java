package com.xinchen.tool.perftest.sequenced;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestDisruptor;
import com.xinchen.tool.perftest.support.FizzBuzzEvent;
import com.xinchen.tool.perftest.support.FizzBuzzEventHandler;
import com.xinchen.tool.perftest.support.FizzBuzzStep;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.lmax.disruptor.RingBuffer.createSingleProducer;
import static com.xinchen.tool.perftest.support.PerfTestUtil.failIfNot;

/**
 *
 * 给你一个整数n. 从 1 到 n 按照下面的规则打印每个数：
 *
 * 如果这个数被3整除，打印fizz.
 *
 * 如果这个数被5整除，打印buzz.
 *
 * 如果这个数能同时被3和5整除，打印fizz buzz.
 *
 * <pre>
 * Produce an event replicated to two event processors and fold back to a single third event processor.
 *
 *           +-----+
 *    +----->| EP1 |------+
 *    |      +-----+      |
 *    |                   v
 * +----+              +-----+
 * | P1 |              | EP3 |
 * +----+              +-----+
 *    |                   ^
 *    |      +-----+      |
 *    +----->| EP2 |------+
 *           +-----+
 *
 * Disruptor:
 * ==========
 *                    track to prevent wrap
 *              +-------------------------------+
 *              |                               |
 *              |                               v
 * +----+    +====+               +=====+    +-----+
 * | P1 |--->| RB |<--------------| SB2 |<---| EP3 |
 * +----+    +====+               +=====+    +-----+
 *      claim   ^  get               |   waitFor
 *              |                    |
 *           +=====+    +-----+      |
 *           | SB1 |<---| EP1 |<-----+
 *           +=====+    +-----+      |
 *              ^                    |
 *              |       +-----+      |
 *              +-------| EP2 |<-----+
 *             waitFor  +-----+
 *
 * P1  - Publisher 1
 * RB  - RingBuffer
 * SB1 - SequenceBarrier 1
 * EP1 - EventProcessor 1
 * EP2 - EventProcessor 2
 * SB2 - SequenceBarrier 2
 * EP3 - EventProcessor 3
 *
 * </pre>
 *
 * @author xinchen
 * @version 1.0
 * @date 29/06/2020 16:24
 */
public final class OneToThreeDiamondSequencedThroughputTest extends AbstractPerfTestDisruptor {
    private static final int NUM_EVENT_PROCESSORS = 3;
    private static final int BUFFER_SIZE = 1024 * 8;
    private static final long ITERATIONS = 1000L * 1000L * 100L;
    private final ExecutorService executor = Executors.newFixedThreadPool(NUM_EVENT_PROCESSORS, DaemonThreadFactory.INSTANCE);

    private final long expectedResult;

    {
        long temp = 0L;

        for (long i = 0; i < ITERATIONS; i++) {
            boolean fizz = 0 == (i % 3L);
            boolean buzz = 0 == (i % 5L);

            if (fizz && buzz) {
                ++temp;
            }
        }
        // 这里统计同时被3和5整除的数
        expectedResult = temp;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final RingBuffer<FizzBuzzEvent> ringBuffer = createSingleProducer(FizzBuzzEvent.EVENT_FACTORY, BUFFER_SIZE, new YieldingWaitStrategy());

    private final SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

    private final FizzBuzzEventHandler fizzHandler = new FizzBuzzEventHandler(FizzBuzzStep.FIZZ);
    private final BatchEventProcessor<FizzBuzzEvent> batchProcessorFizz = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, fizzHandler);


    private final FizzBuzzEventHandler buzzHandler = new FizzBuzzEventHandler(FizzBuzzStep.BUZZ);
    private final BatchEventProcessor<FizzBuzzEvent> batchProcessorBuzz = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, buzzHandler);



    private final SequenceBarrier sequenceBarrierFizzBuzz = ringBuffer.newBarrier(batchProcessorFizz.getSequence(), batchProcessorBuzz.getSequence());
    /** 这个事件处理需要等待 fizzHandler 和 buzzHandler 处理完成*/
    private final FizzBuzzEventHandler fizzBuzzHandler = new FizzBuzzEventHandler(FizzBuzzStep.FIZZ_BUZZ);
    private final BatchEventProcessor<FizzBuzzEvent> batchProcessorFizzBuzz = new BatchEventProcessor<>(ringBuffer, sequenceBarrierFizzBuzz, fizzBuzzHandler);

    {
        ringBuffer.addGatingSequences(batchProcessorFizzBuzz.getSequence());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 4;
    }

    @Override
    protected long runDisruptorPass() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        // 此handler处理的事件总数为ITERATIONS
        fizzBuzzHandler.reset(latch, batchProcessorFizzBuzz.getSequence().get() + ITERATIONS);

        executor.submit(batchProcessorFizz);
        executor.submit(batchProcessorBuzz);
        executor.submit(batchProcessorFizzBuzz);

        long start = System.currentTimeMillis();

        for (long i = 0; i < ITERATIONS; i++) {
            long sequence = ringBuffer.next();
            ringBuffer.get(sequence).setValue(i);
            ringBuffer.publish(sequence);
        }

        latch.await();
        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);

        batchProcessorFizz.halt();
        batchProcessorBuzz.halt();
        batchProcessorFizzBuzz.halt();

        failIfNot(expectedResult, fizzBuzzHandler.getFizzBuzzCounter());

        return opsPerSecond;
    }

    public static void main(String[] args) throws Exception {
        new OneToThreeDiamondSequencedThroughputTest().testImplementations();
    }
}
