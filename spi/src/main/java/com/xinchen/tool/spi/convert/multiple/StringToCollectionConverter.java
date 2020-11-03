package com.xinchen.tool.spi.convert.multiple;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The class to convert {@link String} to {@link Collection}-based value
 *
 */
public class StringToCollectionConverter extends StringToIterableConverter<Collection> {

    @Override
    protected Collection createMultiValue(int size, Class<?> multiValueType) {
        return new ArrayList(size);
    }
}
