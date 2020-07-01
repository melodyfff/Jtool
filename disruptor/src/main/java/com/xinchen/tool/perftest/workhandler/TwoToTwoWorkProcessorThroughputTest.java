package com.xinchen.tool.perftest.workhandler;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkProcessor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.xinchen.tool.perftest.AbstractPerfTestDisruptor;
import com.xinchen.tool.perftest.support.PerfTestUtil;
import com.xinchen.tool.perftest.support.ValueWorkHandlerAddition;
import com.xinchen.tool.perftest.support.ValueEvent;
import com.xinchen.tool.perftest.support.ValuePublisher;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.LockSupport;

import static com.lmax.disruptor.RingBuffer.createMultiProducer;
import static com.xinchen.tool.perftest.support.PerfTestUtil.failIfNot;

/**
 *
 * <pre>
 * Sequence a series of events from multiple publishers going to multiple work processors.
 *
 * +----+                  +-----+
 * | P1 |---+          +-->| WP1 |
 * +----+   |  +-----+ |   +-----+
 *          +->| RB1 |-+
 * +----+   |  +-----+ |   +-----+
 * | P2 |---+          +-->| WP2 |
 * +----+                  +-----+
 *
 * P1  - Publisher 1
 * P2  - Publisher 2
 * RB  - RingBuffer
 * WP1 - EventProcessor 1
 * WP2 - EventProcessor 2
 * </pre>
 *
 * @author xinchen
 * @version 1.0
 * @date 01/07/2020 14:24
 */
public class TwoToTwoWorkProcessorThroughputTest extends AbstractPerfTestDisruptor {
    private static final int NUM_PUBLISHERS = 2;
    private static final int BUFFER_SIZE = 1024 * 64;
    private static final long ITERATIONS = 1000L * 1000L * 1L;
    private final ExecutorService executor = Executors.newFixedThreadPool(NUM_PUBLISHERS + 2, DaemonThreadFactory.INSTANCE);
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(NUM_PUBLISHERS + 1);

    ///////////////////////////////////////////////////////////////////////////////////////////////

    // BusySpinWaitStrategy 忙碌旋转策略 此策略将使用CPU资源来避免可能导致延迟抖动的系统调用。 当线程可以绑定到特定的CPU内核时，最好使用它

    private final RingBuffer<ValueEvent> ringBuffer = createMultiProducer(ValueEvent.EVENT_FACTORY, BUFFER_SIZE, new BusySpinWaitStrategy());

    private final SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();


    /**  workSequence里面存储的其实是下次要处理的event序号（还未被执行），初始化值始终应该为-1 */
    private final Sequence workSequence = new Sequence(-1);

    private final ValueWorkHandlerAddition[] handlers = new ValueWorkHandlerAddition[2];

    {
        // 累加event中的事件值,并保存结果
        handlers[0] = new ValueWorkHandlerAddition();
        handlers[1] = new ValueWorkHandlerAddition();
    }

    @SuppressWarnings("unchecked")
    private final WorkProcessor<ValueEvent>[] workProcessors = new WorkProcessor[2];

    {

        // 这里是单独对WorkProcessor初始化，并自己丢入需要执行的线程池里面
        workProcessors[0] = new WorkProcessor<>(
                ringBuffer,
                sequenceBarrier,
                handlers[0],
                new IgnoreExceptionHandler(),
                workSequence);

        workProcessors[1] = new WorkProcessor<>(
                ringBuffer,
                sequenceBarrier,
                handlers[1],
                new IgnoreExceptionHandler(),
                workSequence);
    }

    private final ValuePublisher[] valuePublishers = new ValuePublisher[NUM_PUBLISHERS];

    {
        for (int i = 0; i < NUM_PUBLISHERS; i++) {
            valuePublishers[i] = new ValuePublisher(cyclicBarrier, ringBuffer, ITERATIONS);
        }

        ringBuffer.addGatingSequences(workProcessors[0].getSequence(),workProcessors[0].getSequence());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int getRequiredProcessorCount() {
        return 4;
    }

    @Override
    protected long runDisruptorPass() throws Exception {
        // 推算ringBuffer中 Cursor的位置
        long expected = ringBuffer.getCursor() + (NUM_PUBLISHERS * ITERATIONS);


//        workSequence.set(ringBuffer.getCursor());

        Future<?>[] futures = new Future[NUM_PUBLISHERS];

        // 提交生产者
        for (int i = 0; i < NUM_PUBLISHERS; i++) {
            futures[i] = executor.submit(valuePublishers[i]);
        }

        // 提交消费者
        for (WorkProcessor<ValueEvent> processor : workProcessors) {
            executor.submit(processor);
        }

        long start = System.currentTimeMillis();

        // 开始生产event
        cyclicBarrier.await();


        // 确保生产着线程执行完成
        for (int i = 0; i < NUM_PUBLISHERS; i++) {
            futures[i].get();
        }

        // 等待一些处理的慢的消费者执行完成
        // WorkProcessor中的workSequence里面存储的其实是下次要处理的event序号（还未被执行），对应的WorkProcessor里面的sequence，其实是已经处理过的事件的序号
        while (workSequence.get() < expected) {
            LockSupport.parkNanos(1L);
        }


        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);

        Thread.sleep(1000);

        for (WorkProcessor<ValueEvent> processor : workProcessors) {
            processor.halt();
        }

        return opsPerSecond;
    }

    public static void main(String[] args) throws Exception {
        new TwoToTwoWorkProcessorThroughputTest().testImplementations();
    }
}
