package com.xinchen.tool.spi.convert.multiple;

import java.util.ArrayList;
import java.util.List;

/**
 * The class to convert {@link String} to {@link List}-based value
 *
 */
public class StringToListConverter extends StringToIterableConverter<List> {

    @Override
    protected List createMultiValue(int size, Class<?> multiValueType) {
        return new ArrayList(size);
    }
}
