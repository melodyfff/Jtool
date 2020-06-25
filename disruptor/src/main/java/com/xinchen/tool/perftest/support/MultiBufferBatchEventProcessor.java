package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.DataProvider;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 多个RingBuffer事件批处理进程
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/6/25 23:01
 */
public class MultiBufferBatchEventProcessor<T> implements EventProcessor {
    /** 控制EventProcessor运行状态 */
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final DataProvider<T>[] providers;
    private final SequenceBarrier[] barriers;
    private final EventHandler<T> handler;
    private final Sequence[] sequences;
    private long count;

    public MultiBufferBatchEventProcessor(
            DataProvider<T>[] providers,
            SequenceBarrier[] barriers,
            EventHandler<T> handler) {
        if (providers.length != barriers.length) {
            throw new IllegalArgumentException();
        }

        this.providers = providers;
        this.barriers = barriers;
        this.handler = handler;

        this.sequences = new Sequence[providers.length];
        for (int i = 0; i < sequences.length; i++) {
            // Sequence默认值其实就是-1L
            sequences[i] = new Sequence(-1);
        }
    }

    @Override
    public void run() {
        if (!isRunning.compareAndSet(false, true)) {
            throw new RuntimeException("Already running");
        }

        // When start run , Clear the current alert status.
        for (SequenceBarrier barrier : barriers) {
            barrier.clearAlert();
        }

        final int barrierLength = barriers.length;

        while (true) {
            try {
                for (int i = 0; i < barrierLength; i++) {
                    // 写死等待sequence为-1？
                    // 观察com.lmax.disruptor.ProcessingSequenceBarrier.waitFor(long)可知道，主要是根据不同的waitStrategy获得availableSequence
                    // 由于写死为-1，在YieldingWaitStrategy策略下，SingleProducerSequencer时，其实是直接返回waitStrategy.waitFor(sequence, cursorSequence, dependentSequence, this); 所得结果
                    long available = barriers[i].waitFor(-1);

                    Sequence sequence = sequences[i];

                    long nextSequence = sequence.get() + 1;

                    for (long l = nextSequence; l <= available; l++) {
                        handler.onEvent(providers[i].get(l), l, nextSequence == available);
                    }

                    sequence.set(available);

                    count += available - nextSequence + 1;
                }

                // 让出CPU
                Thread.yield();
            } catch (AlertException e) {
                if (!isRunning()) {
                    break;
                }
            } catch (InterruptedException | TimeoutException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    @Override
    public Sequence getSequence() {
        throw new UnsupportedOperationException();
    }

    public long getCount() {
        return count;
    }

    public Sequence[] getSequences() {
        return sequences;
    }

    @Override
    public void halt() {
        isRunning.set(false);
        // 这里为什么只对一个barriers修改了alert?
        barriers[0].alert();
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }
}
