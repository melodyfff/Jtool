package com.xinchen.tool.perftest.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author xinchen
 * @version 1.0
 * @date 01/07/2020 12:32
 */
public class ValueQueueProcessorMutationr implements Runnable {
    private volatile boolean running;
    private long value;
    private long sequence;
    private CountDownLatch latch;

    private final BlockingQueue<Long> blockingQueue;
    private final Operation operation;
    private final long count;

    public ValueQueueProcessorMutationr(
            final BlockingQueue<Long> blockingQueue, final Operation operation, final long count) {
        this.blockingQueue = blockingQueue;
        this.operation = operation;
        this.count = count;
    }

    public long getValue() {
        return value;
    }

    public void reset(final CountDownLatch latch) {
        value = 0L;
        sequence = 0L;
        this.latch = latch;
    }

    public void halt() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
        while (true) {
            try {
                long value = blockingQueue.take();
                this.value = operation.op(this.value, value);

                if (sequence++ == count) {
                    latch.countDown();
                }
            } catch (InterruptedException ex) {
                if (!running) {
                    break;
                }
            }
        }
    }
}
