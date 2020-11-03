package com.xinchen.tool.spi.convert.multiple;


import com.xinchen.tool.spi.convert.StringConverter;
import com.xinchen.tool.spi.utils.ClassUtils;

import java.util.Collection;
import java.util.Optional;

import static com.xinchen.tool.spi.convert.Converter.getConverter;
import static com.xinchen.tool.spi.utils.ClassUtils.isAssignableFrom;
import static com.xinchen.tool.spi.utils.TypeUtils.findActualTypeArgument;


/**
 * The class to convert {@link String} to {@link Iterable}-based value
 *
 */
public abstract class StringToIterableConverter<T extends Iterable> implements StringToMultiValueConverter {

    @Override
    public boolean accept(Class<String> type, Class<?> multiValueType) {
        return isAssignableFrom(getSupportedType(), multiValueType);
    }

    @Override
    public final Object convert(String[] segments, int size, Class<?> multiValueType, Class<?> elementType) {

        Optional<StringConverter> stringConverter = getStringConverter(elementType);

        return stringConverter.map(converter -> {

            T convertedObject = createMultiValue(size, multiValueType);

            if (convertedObject instanceof Collection) {
                Collection collection = (Collection) convertedObject;
                for (int i = 0; i < size; i++) {
                    String segment = segments[i];
                    Object element = converter.convert(segment);
                    collection.add(element);
                }
                return collection;
            }

            return convertedObject;
        }).orElse(null);
    }

    protected abstract T createMultiValue(int size, Class<?> multiValueType);

    protected Optional<StringConverter> getStringConverter(Class<?> elementType) {
        StringConverter converter = (StringConverter) getConverter(String.class, elementType);
        return Optional.ofNullable(converter);
    }

    protected final Class<T> getSupportedType() {
        return findActualTypeArgument(getClass(), StringToIterableConverter.class, 0);
    }

    @Override
    public final int getPriority() {
        int level = ClassUtils.getAllInterfaces(getSupportedType(), type ->
                isAssignableFrom(Iterable.class, type)).size();
        return MIN_PRIORITY - level;
    }
}
