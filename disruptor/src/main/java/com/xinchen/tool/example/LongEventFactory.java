package com.xinchen.tool.example;

import com.lmax.disruptor.EventFactory;

/**
 *
 * EventFactory perform the events
 *
 * @author xinchen
 * @version 1.0
 * @date 15/04/2020 15:59
 */
public class LongEventFactory implements EventFactory<LongEvent> {

    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
