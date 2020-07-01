package com.xinchen.tool.perftest.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author xinchen
 * @version 1.0
 * @date 01/07/2020 13:42
 */
public class FunctionQueueProcessor implements Runnable {
    private final FunctionStep functionStep;
    private final BlockingQueue<long[]> stepOneQueue;
    private final BlockingQueue<Long> stepTwoQueue;
    private final BlockingQueue<Long> stepThreeQueue;
    private final long count;

    private volatile boolean running;
    private long stepThreeCounter;
    private long sequence;
    private CountDownLatch latch;

    public FunctionQueueProcessor(
            final FunctionStep functionStep,
            final BlockingQueue<long[]> stepOneQueue,
            final BlockingQueue<Long> stepTwoQueue,
            final BlockingQueue<Long> stepThreeQueue,
            final long count) {
        this.functionStep = functionStep;
        this.stepOneQueue = stepOneQueue;
        this.stepTwoQueue = stepTwoQueue;
        this.stepThreeQueue = stepThreeQueue;
        this.count = count;
    }

    public long getStepThreeCounter() {
        return stepThreeCounter;
    }

    public void reset(final CountDownLatch latch) {
        stepThreeCounter = 0L;
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
                switch (functionStep) {
                    case ONE: {
                        long[] values = stepOneQueue.take();
                        stepTwoQueue.put(values[0] + values[1]);
                        break;
                    }

                    case TWO: {
                        Long value = stepTwoQueue.take();
                        stepThreeQueue.put(value + 3);
                        break;
                    }

                    case THREE: {
                        long testValue = stepThreeQueue.take();
                        if ((testValue & 4L) == 4L) {
                            ++stepThreeCounter;
                        }
                        break;
                    }
                    default:
                        break;

                }

                // 这里主要是统一交给最后一步进行控制进度，所以latch可能为null
                if (null != latch && sequence++ == count) {
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

