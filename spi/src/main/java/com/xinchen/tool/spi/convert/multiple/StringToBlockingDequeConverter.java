package com.xinchen.tool.spi.convert.multiple;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The class to convert {@link String} to {@link BlockingDeque}-based value
 *
 */
public class StringToBlockingDequeConverter extends StringToIterableConverter<BlockingDeque> {

    @Override
    protected BlockingDeque createMultiValue(int size, Class<?> multiValueType) {
        return new LinkedBlockingDeque(size);
    }
}
