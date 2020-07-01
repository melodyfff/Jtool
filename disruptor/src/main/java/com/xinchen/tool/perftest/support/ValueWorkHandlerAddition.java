package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.WorkHandler;

/**
 * @author xinchen
 * @version 1.0
 * @date 01/07/2020 14:27
 */
public class ValueWorkHandlerAddition implements WorkHandler<ValueEvent> {
    private long total;

    @Override
    public void onEvent(ValueEvent event) throws Exception {
        long value = event.getValue();
        total += value;
    }

    public long getTotal() {
        return total;
    }
}