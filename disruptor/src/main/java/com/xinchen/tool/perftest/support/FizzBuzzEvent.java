package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.EventFactory;

/**
 *
 * 给你一个整数n. 从 1 到 n 按照下面的规则打印每个数：
 *
 * 如果这个数被3整除，打印fizz.
 *
 * 如果这个数被5整除，打印buzz.
 *
 * 如果这个数能同时被3和5整除，打印fizz buzz.
 *
 * @author xinchen
 * @version 1.0
 * @date 29/06/2020 16:25
 */
public final class FizzBuzzEvent {
    private boolean fizz = false;
    private boolean buzz = false;
    private long value = 0;

    public long getValue() {
        return value;
    }

    public void setValue(final long value) {
        fizz = false;
        buzz = false;
        this.value = value;
    }

    public boolean isFizz() {
        return fizz;
    }

    public void setFizz(final boolean fizz) {
        this.fizz = fizz;
    }

    public boolean isBuzz() {
        return buzz;
    }

    public void setBuzz(final boolean buzz) {
        this.buzz = buzz;
    }

    public static final EventFactory<FizzBuzzEvent> EVENT_FACTORY = new EventFactory<FizzBuzzEvent>() {
        @Override
        public FizzBuzzEvent newInstance() {
            return new FizzBuzzEvent();
        }
    };
}