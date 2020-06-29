package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.EventFactory;

/**
 * @author xinchen
 * @version 1.0
 * @date 29/06/2020 17:06
 */
public class FunctionEvent {
    private long operandOne;
    private long operandTwo;
    private long stepOneResult;
    private long stepTwoResult;

    public long getOperandOne() {
        return operandOne;
    }

    public void setOperandOne(final long operandOne) {
        this.operandOne = operandOne;
    }

    public long getOperandTwo() {
        return operandTwo;
    }

    public void setOperandTwo(final long operandTwo) {
        this.operandTwo = operandTwo;
    }

    public long getStepOneResult() {
        return stepOneResult;
    }

    public void setStepOneResult(final long stepOneResult) {
        this.stepOneResult = stepOneResult;
    }

    public long getStepTwoResult() {
        return stepTwoResult;
    }

    public void setStepTwoResult(final long stepTwoResult) {
        this.stepTwoResult = stepTwoResult;
    }

    public static final EventFactory<FunctionEvent> EVENT_FACTORY = FunctionEvent::new;
}

