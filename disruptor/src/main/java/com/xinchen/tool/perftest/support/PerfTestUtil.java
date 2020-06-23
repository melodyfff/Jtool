package com.xinchen.tool.perftest.support;

/**
 *
 * PerfTestUtil
 *
 * @author xinchen
 * @version 1.0
 * @date 23/06/2020 10:47
 */
public final class PerfTestUtil {

    public static long accumulatedAddition(final long iterations) {
        // accumulated addition
        long temp = 0L;
        for (long i = 0L; i < iterations; i++) {
            temp += i;
        }
        return temp;
    }

    public static void failIf(long a, long b) {
        if (a == b) {
            throw new RuntimeException();
        }
    }

    public static void failIfNot(long a, long b) {
        if (a != b) {
            throw new RuntimeException();
        }
    }
}