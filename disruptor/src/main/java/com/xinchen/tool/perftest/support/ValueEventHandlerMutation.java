package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.EventHandler;
import com.xinchen.tool.perftest.support.util.PaddedLong;

import java.util.concurrent.CountDownLatch;

/**
 *
 * 复合操作事件处理，持有计算结果
 *
 * @author xinchen
 * @version 1.0
 * @date 24/06/2020 16:07
 */
public class ValueEventHandlerMutation implements EventHandler<ValueEvent> {
    private final Operation operation;
    private final PaddedLong value = new PaddedLong();
    private long count;
    private CountDownLatch latch;

    public ValueEventHandlerMutation(final Operation operation) {
        this.operation = operation;
    }

    public long getValue() {
        return value.get();
    }

    public void reset(final CountDownLatch latch, final long expectedCount) {
        value.set(0L);
        this.latch = latch;
        count = expectedCount;
    }

    @Override
    public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
        // 根据不同的Operation执行不同的操作
        value.set(operation.op(value.get(), event.getValue()));

        if (count == sequence) {
            latch.countDown();
        }
    }
}
