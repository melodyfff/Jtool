package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.EventHandler;
import com.xinchen.tool.perftest.support.util.PaddedLong;

import java.util.concurrent.CountDownLatch;

/**
 * Value Addition EventHandler
 *
 * @author xinchen
 * @version 1.0
 * @date 23/06/2020 10:53
 */
public final class ValueAdditionEventHandler implements EventHandler<ValueEvent> {
    /** 根据cache line的特性，使用填充 */
    private final PaddedLong value = new PaddedLong();
    private long count;
    private CountDownLatch latch;

    public long getValue() {
        return value.get();
    }

    /**
     * 重置
     * @param latch CountDownLatch
     * @param expectedCount 期待执行的次数
     */
    public void reset(final CountDownLatch latch, final long expectedCount) {
        value.set(0L);
        this.latch = latch;
        count = expectedCount;
    }

    @Override
    public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
        value.set(value.get() + event.getValue());

        if (count == sequence) {
            latch.countDown();
        }
    }
}
