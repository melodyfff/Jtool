package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.EventHandler;
import com.xinchen.tool.perftest.support.util.PaddedLong;

import java.util.concurrent.CountDownLatch;

/**
 * @author xinchen
 * @version 1.0
 * @date 24/06/2020 11:10
 */
public class LongArrayEventHandler implements EventHandler<long[]> {
    private final PaddedLong value = new PaddedLong();
    private long count;
    private CountDownLatch latch;

    public long getValue() {
        return value.get();
    }

    public void reset(final CountDownLatch latch, final long expectedCount) {
        value.set(0L);
        this.latch = latch;
        count = expectedCount;
    }

    @Override
    public void onEvent(final long[] event, final long sequence, final boolean endOfBatch) throws Exception {
        // 将event[]中的值进行累加，并保持最终结果value
        for (int i = 0; i < event.length; i++) {
            value.set(value.get() + event[i]);
        }

        if (--count == 0) {
            latch.countDown();
        }
    }
}
