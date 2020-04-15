package com.xinchen.tool.example;

import com.lmax.disruptor.EventHandler;

/**
 *
 * a consumer that will handle these events
 *
 * In our case all we want to do is print the value out the the console.
 *
 * @author xinchen
 * @version 1.0
 * @date 15/04/2020 16:02
 */
public class LongEventHandler implements EventHandler<LongEvent> {
    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("Event: "+event);
    }
}
