package com.xinchen.tool.perftest.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/6/27 21:41
 */
public final class ValueQueueProcessorAddition implements Runnable {
    private volatile boolean running;
    private long value;
    private long sequence;
    private CountDownLatch latch;

    private final BlockingQueue<Long> blockingQueue;
    private final long count;

    public ValueQueueProcessorAddition(final BlockingQueue<Long> blockingQueue, final long count) {
        this.blockingQueue = blockingQueue;
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
                // 从BlockingQueue队列中取值累加
                long value = blockingQueue.take().longValue();
                this.value += value;

                // 控制执行次数
                if (++sequence == count) {
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

