package com.xinchen.tool.perftest.support.util;

/**
 * 当出现伪共享时，缓存行填充
 * <p>
 * Cache line padded long variable to be used when false sharing maybe an issue.
 *
 * @author xinchen
 * @version 1.0
 * @date 23/06/2020 10:56
 */
public final class PaddedLong extends MutableLong {
    public volatile long p1, p2, p3, p4, p5, p6 = 7L;

    /**
     * Default constructor
     */
    public PaddedLong() {
    }

    /**
     * Construct with an initial value.
     *
     * @param initialValue for construction
     */
    public PaddedLong(final long initialValue) {
        super(initialValue);
    }

    public long sumPaddingToPreventOptimisation() {
        return p1 + p2 + p3 + p4 + p5 + p6;
    }
}
