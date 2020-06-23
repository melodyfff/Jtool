package com.xinchen.tool.perftest.support.util;

/**
 * Mutable（可变的） Long
 * <p>
 * Holder class for a long value.
 *
 * @author xinchen
 * @version 1.0
 * @date 23/06/2020 10:56
 */
public class MutableLong {
    private long value = 0L;

    /**
     * Default constructor
     */
    public MutableLong() {
    }

    /**
     * Construct the holder with initial value.
     *
     * @param initialValue to be initially set.
     */
    public MutableLong(final long initialValue) {
        this.value = initialValue;
    }

    /**
     * Get the long value.
     *
     * @return the long value.
     */
    public long get() {
        return value;
    }

    /**
     * Set the long value.
     *
     * @param value to set.
     */
    public void set(final long value) {
        this.value = value;
    }

    /**
     * Increments the value
     */
    public void increment() {
        value++;
    }
}
