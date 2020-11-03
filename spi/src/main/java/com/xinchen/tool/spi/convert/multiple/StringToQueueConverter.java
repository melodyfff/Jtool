package com.xinchen.tool.spi.convert.multiple;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

/**
 * The class to convert {@link String} to {@link Deque}-based value
 *
 */
public class StringToQueueConverter extends StringToIterableConverter<Queue> {

    @Override
    protected Queue createMultiValue(int size, Class<?> multiValueType) {
        return new ArrayDeque(size);
    }
}
