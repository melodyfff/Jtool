package com.xinchen.tool.spi.convert.multiple;


import com.xinchen.tool.spi.convert.Converter;

import java.lang.reflect.Array;

import static java.lang.reflect.Array.newInstance;

/**
 * The class to convert {@link String} to array-type object
 *
 */
public class StringToArrayConverter implements StringToMultiValueConverter {

    @Override
    public boolean accept(Class<String> type, Class<?> multiValueType) {
        if (multiValueType != null && multiValueType.isArray()) {
            return true;
        }
        return false;
    }

    @Override
    public Object convert(String[] segments, int size, Class<?> targetType, Class<?> elementType) {

        Class<?> componentType = targetType.getComponentType();

        Converter converter = Converter.getConverter(String.class, componentType);

        Object array = newInstance(componentType, size);

        for (int i = 0; i < size; i++) {
            Array.set(array, i, converter.convert(segments[i]));
        }

        return array;
    }


    @Override
    public int getPriority() {
        return MIN_PRIORITY;
    }
}
