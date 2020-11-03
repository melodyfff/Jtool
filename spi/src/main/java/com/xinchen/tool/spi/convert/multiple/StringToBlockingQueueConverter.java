package com.xinchen.tool.spi.convert.multiple;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

/**
 * The class to convert {@link String} to {@link BlockingDeque}-based value
 *
 */
public class StringToBlockingQueueConverter extends StringToIterableConverter<BlockingQueue> {

    @Override
    protected BlockingQueue createMultiValue(int size, Class<?> multiValueType) {
        return new ArrayBlockingQueue(size);
    }
}
