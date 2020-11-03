package com.xinchen.tool.spi.convert.multiple;

import java.util.HashSet;
import java.util.Set;

/**
 * The class to convert {@link String} to {@link Set}-based value
 *
 */
public class StringToSetConverter extends StringToIterableConverter<Set> {

    @Override
    protected Set createMultiValue(int size, Class<?> multiValueType) {
        return new HashSet(size);
    }
}
