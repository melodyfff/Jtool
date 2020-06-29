package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.EventHandler;
import com.xinchen.tool.perftest.support.util.PaddedLong;

import java.util.concurrent.CountDownLatch;

/**
 * @author xinchen
 * @version 1.0
 * @date 29/06/2020 17:12
 */
public class FunctionEventHandler implements EventHandler<FunctionEvent> {
    private final FunctionStep functionStep;
    private final PaddedLong stepThreeCounter = new PaddedLong();
    private long count;
    private CountDownLatch latch;

    public FunctionEventHandler(final FunctionStep functionStep) {
        this.functionStep = functionStep;
    }

    public long getStepThreeCounter() {
        return stepThreeCounter.get();
    }

    public void reset(final CountDownLatch latch, final long expectedCount) {
        stepThreeCounter.set(0L);
        this.latch = latch;
        count = expectedCount;
    }

    @Override
    public void onEvent(final FunctionEvent event, final long sequence, final boolean endOfBatch) throws Exception {
        switch (functionStep) {
            case ONE:
                event.setStepOneResult(event.getOperandOne() + event.getOperandTwo());
                break;

            case TWO:
                event.setStepTwoResult(event.getStepOneResult() + 3L);
                break;

            case THREE:
                if ((event.getStepTwoResult() & 4L) == 4L) {
                    stepThreeCounter.set(stepThreeCounter.get() + 1L);
                }
                break;
            default:
                break;
        }

        if (latch != null && count == sequence) {
            latch.countDown();
        }
    }
}
