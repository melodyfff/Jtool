package com.xinchen.tool.perftest.support.util;

import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.util.Util;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author xinchen
 * @version 1.0
 * @date 23/06/2020 11:19
 */
public final class UtilTest {
    @Test
    public void shouldReturnNextPowerOfTwo() {
        // ceiling上限
        // 下一个2的幂
        int powerOfTwo = Util.ceilingNextPowerOfTwo(1000);

        Assert.assertEquals(1024, powerOfTwo);
    }

    @Test
    public void shouldReturnExactPowerOfTwo() {
        int powerOfTwo = Util.ceilingNextPowerOfTwo(1024);
        Assert.assertEquals(1024, powerOfTwo);
    }

    @Test
    public void shouldReturnMinimumSequence() {
        // 获取最小sequence
        final Sequence[] sequences = {new Sequence(7L), new Sequence(3L), new Sequence(12L)};
        Assert.assertEquals(3L, Util.getMinimumSequence(sequences));
    }

    @Test
    public void shouldReturnLongMaxWhenNoEventProcessors() {
        final Sequence[] sequences = new Sequence[0];
        Assert.assertEquals(Long.MAX_VALUE, Util.getMinimumSequence(sequences));
    }
}