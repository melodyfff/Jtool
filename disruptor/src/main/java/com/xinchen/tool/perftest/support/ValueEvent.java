package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.EventFactory;

/**
 *
 * value event
 *
 * @author xinchen
 * @version 1.0
 * @date 23/06/2020 10:51
 */
public final class ValueEvent {
    private long value;

    public long getValue() {
        return value;
    }

    public void setValue(final long value) {
        this.value = value;
    }

    public static final EventFactory<ValueEvent> EVENT_FACTORY = new EventFactory<ValueEvent>() {
        @Override
        public ValueEvent newInstance() {
            // create ValueEvent
            return new ValueEvent();
        }
    };
}