package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.EventHandler;
import com.xinchen.tool.perftest.support.util.PaddedLong;

import java.util.concurrent.CountDownLatch;

/**
 * 给你一个整数n. 从 1 到 n 按照下面的规则打印每个数：
 * <p>
 * 如果这个数被3整除，打印fizz.
 * <p>
 * 如果这个数被5整除，打印buzz.
 * <p>
 * 如果这个数能同时被3和5整除，打印fizz buzz.
 *
 * @author xinchen
 * @version 1.0
 * @date 29/06/2020 16:28
 */
public class FizzBuzzEventHandler implements EventHandler<FizzBuzzEvent> {
    private final FizzBuzzStep fizzBuzzStep;
    private final PaddedLong fizzBuzzCounter = new PaddedLong();
    private long count;
    private CountDownLatch latch;

    public FizzBuzzEventHandler(final FizzBuzzStep fizzBuzzStep) {
        this.fizzBuzzStep = fizzBuzzStep;
    }

    public void reset(final CountDownLatch latch, final long expectedCount) {
        fizzBuzzCounter.set(0L);
        this.latch = latch;
        count = expectedCount;
    }

    public long getFizzBuzzCounter() {
        return fizzBuzzCounter.get();
    }

    @Override
    public void onEvent(final FizzBuzzEvent event, final long sequence, final boolean endOfBatch) throws Exception {
        switch (fizzBuzzStep) {
            case FIZZ:
                if (0 == (event.getValue() % 3)) {
                    event.setFizz(true);
                }
                break;
            case BUZZ:
                if (0 == (event.getValue() % 5)) {
                    event.setBuzz(true);
                }
                break;
            case FIZZ_BUZZ:
                if (event.isFizz() && event.isBuzz()) {
                    fizzBuzzCounter.set(fizzBuzzCounter.get() + 1L);
                }
                break;
            default:
                break;
        }

        if (latch != null && count == sequence) {
            latch.countDown();
        }
    }
}
