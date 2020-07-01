package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.WorkHandler;
import com.xinchen.tool.perftest.support.util.PaddedLong;

/**
 * @author xinchen
 * @version 1.0
 * @date 01/07/2020 14:19
 */
public class EventCountingWorkHandler implements WorkHandler<ValueEvent> {
    private final PaddedLong[] counters;
    private final int index;

    public EventCountingWorkHandler(final PaddedLong[] counters, final int index) {
        this.counters = counters;
        this.index = index;
    }

    @Override
    public void onEvent(final ValueEvent event) throws Exception {
        counters[index].set(counters[index].get() + 1L);
    }
}

