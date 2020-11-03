package com.xinchen.tool.spi.convert.multiple;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The class to convert {@link String} to {@link Deque}-based value
 *
 */
public class StringToDequeConverter extends StringToIterableConverter<Deque> {

    @Override
    protected Deque createMultiValue(int size, Class<?> multiValueType) {
        return new ArrayDeque(size);
    }
}
