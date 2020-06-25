package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.RingBuffer;

import java.util.concurrent.CyclicBarrier;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/6/25 12:51
 */
public final class ValuePublisherBatch implements Runnable {
    private final CyclicBarrier cyclicBarrier;
    private final RingBuffer<ValueEvent> ringBuffer;
    private final long iterations;
    private final int batchSize;

    public ValuePublisherBatch(
            final CyclicBarrier cyclicBarrier,
            final RingBuffer<ValueEvent> ringBuffer,
            final long iterations,
            final int batchSize) {
        this.cyclicBarrier = cyclicBarrier;
        this.ringBuffer = ringBuffer;
        this.iterations = iterations;
        this.batchSize = batchSize;
    }

    @Override
    public void run() {
        try {
            cyclicBarrier.await();

            for (long i = 0; i < iterations; i += batchSize) {
                long hi = ringBuffer.next(batchSize);
                long lo = hi - (batchSize - 1);
                for (long l = lo; l <= hi; l++) {
                    ValueEvent event = ringBuffer.get(l);
                    event.setValue(l);
                }
                ringBuffer.publish(lo, hi);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}