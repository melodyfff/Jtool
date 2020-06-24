package com.xinchen.tool.perftest.support;

/**
 *
 * Operation
 *
 * @author xinchen
 * @version 1.0
 * @date 24/06/2020 16:04
 */
public enum Operation {
    /**  +  */
    ADDITION {
        @Override
        public long op(final long lhs, final long rhs) {
            return lhs + rhs;
        }
    },
    /**  -  */
    SUBTRACTION {
        @Override
        public long op(final long lhs, final long rhs) {
            return lhs - rhs;
        }
    },
    /**  & */
    AND {
        @Override
        public long op(final long lhs, final long rhs) {
            return lhs & rhs;
        }
    };

    public abstract long op(long lhs, long rhs);
}