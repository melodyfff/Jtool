package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.RingBuffer;

import java.util.concurrent.CyclicBarrier;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/6/25 12:49
 */
public final class ValuePublisher implements Runnable {
    private final CyclicBarrier cyclicBarrier;
    private final RingBuffer<ValueEvent> ringBuffer;
    private final long iterations;

    public ValuePublisher(
            final CyclicBarrier cyclicBarrier, final RingBuffer<ValueEvent> ringBuffer, final long iterations) {
        this.cyclicBarrier = cyclicBarrier;
        this.ringBuffer = ringBuffer;
        this.iterations = iterations;
    }

    @Override
    public void run() {
        try {
            cyclicBarrier.await();

            for (long i = 0; i < iterations; i++) {
                long sequence = ringBuffer.next();
                ValueEvent event = ringBuffer.get(sequence);
                event.setValue(i);
                ringBuffer.publish(sequence);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
