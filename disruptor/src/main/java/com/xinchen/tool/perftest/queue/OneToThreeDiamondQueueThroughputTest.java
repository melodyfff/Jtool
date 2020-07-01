package com.xinchen.tool.perftest.queue;

import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestQueue;
import com.xinchen.tool.perftest.support.FizzBuzzQueueProcessor;
import com.xinchen.tool.perftest.support.FizzBuzzStep;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import static com.xinchen.tool.perftest.support.PerfTestUtil.failIfNot;

/**
 *
 * 产生1个event复制到2个事件处理器上处理，然后传递到第3个事件处理器上
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
 *
 * Queue Based:
 * ============
 *                 take       put
 *     put   +====+    +-----+    +====+  take
 *    +----->| Q1 |<---| EP1 |--->| Q3 |<------+
 *    |      +====+    +-----+    +====+       |
 *    |                                        |
 * +----+    +====+    +-----+    +====+    +-----+
 * | P1 |--->| Q2 |<---| EP2 |--->| Q4 |<---| EP3 |
 * +----+    +====+    +-----+    +====+    +-----+
 *
 * P1  - Publisher 1
 * Q1  - Queue 1
 * Q2  - Queue 2
 * Q3  - Queue 3
 * Q4  - Queue 4
 * EP1 - EventProcessor 1
 * EP2 - EventProcessor 2
 * EP3 - EventProcessor 3
 *
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
 * @date 01/07/2020 11:23
 */
public class OneToThreeDiamondQueueThroughputTest extends AbstractPerfTestQueue {
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

        expectedResult = temp;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private final BlockingQueue<Long> fizzInputQueue = new LinkedBlockingQueue<>(BUFFER_SIZE);
    private final BlockingQueue<Long> buzzInputQueue = new LinkedBlockingQueue<>(BUFFER_SIZE);
    private final BlockingQueue<Boolean> fizzOutputQueue = new LinkedBlockingQueue<>(BUFFER_SIZE);
    private final BlockingQueue<Boolean> buzzOutputQueue = new LinkedBlockingQueue<>(BUFFER_SIZE);

    private final FizzBuzzQueueProcessor fizzQueueProcessor = new FizzBuzzQueueProcessor(FizzBuzzStep.FIZZ, fizzInputQueue, buzzInputQueue, fizzOutputQueue, buzzOutputQueue, ITERATIONS);

    private final FizzBuzzQueueProcessor buzzQueueProcessor = new FizzBuzzQueueProcessor(FizzBuzzStep.BUZZ, fizzInputQueue, buzzInputQueue, fizzOutputQueue, buzzOutputQueue, ITERATIONS);

    private final FizzBuzzQueueProcessor fizzBuzzQueueProcessor = new FizzBuzzQueueProcessor(FizzBuzzStep.FIZZ_BUZZ, fizzInputQueue, buzzInputQueue, fizzOutputQueue, buzzOutputQueue, ITERATIONS);

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 4;
    }

    @Override
    protected long runQueuePass() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        fizzBuzzQueueProcessor.reset(latch);

        Future<?>[] futures = new Future[NUM_EVENT_PROCESSORS];
        futures[0] = executor.submit(fizzQueueProcessor);
        futures[1] = executor.submit(buzzQueueProcessor);
        futures[2] = executor.submit(fizzBuzzQueueProcessor);

        long start = System.currentTimeMillis();

        for (long i = 0; i < ITERATIONS; i++) {
            Long value = i;
            fizzInputQueue.put(value);
            buzzInputQueue.put(value);
        }

        latch.await();
        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);

        fizzQueueProcessor.halt();
        buzzQueueProcessor.halt();
        fizzBuzzQueueProcessor.halt();

        for (Future<?> future : futures) {
            future.cancel(true);
        }

        failIfNot(expectedResult, fizzBuzzQueueProcessor.getFizzBuzzCounter());

        return opsPerSecond;
    }

    public static void main(String[] args) throws Exception {
        new OneToThreeDiamondQueueThroughputTest().testImplementations();
    }
}