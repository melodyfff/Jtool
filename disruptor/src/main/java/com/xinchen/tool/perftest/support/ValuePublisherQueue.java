package com.xinchen.tool.perftest.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;

/**
 * @author xinchen
 * @version 1.0
 * @date 01/07/2020 13:56
 */
public class ValuePublisherQueue implements Runnable {
    private final CyclicBarrier cyclicBarrier;
    private final BlockingQueue<Long> blockingQueue;
    private final long iterations;

    public ValuePublisherQueue(
            final CyclicBarrier cyclicBarrier, final BlockingQueue<Long> blockingQueue, final long iterations) {
        this.cyclicBarrier = cyclicBarrier;
        this.blockingQueue = blockingQueue;
        this.iterations = iterations;
    }

    @Override
    public void run() {
        try {
            cyclicBarrier.await();
            for (long i = 0; i < iterations; i++) {
                blockingQueue.put(i);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
