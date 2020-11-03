package com.xinchen.tool.spi.convert.multiple;


import com.xinchen.tool.spi.extension.ExtensionLoader;
import com.xinchen.tool.spi.extension.SPI;
import com.xinchen.tool.spi.lang.Prioritized;

import java.util.Collection;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static com.xinchen.tool.spi.utils.TypeUtils.findActualTypeArgument;

/**
 * An interface to convert the source-typed value to multiple value, e.g , Java array, {@link Collection} or
 * sub-interfaces
 *
 * @param <S> The source type
 */
@SPI
public interface MultiValueConverter<S> extends Prioritized {

    /**
     * Accept the source type and target type or not
     *
     * @param sourceType     the source type
     * @param multiValueType the multi-value type
     * @return if accepted, return <code>true</code>, or <code>false</code>
     */
    boolean accept(Class<S> sourceType, Class<?> multiValueType);

    /**
     * Convert the source to be the multiple value
     *
     * @param source         the source-typed value
     * @param multiValueType the multi-value type
     * @param elementType    the element type
     * @return
     */
    Object convert(S source, Class<?> multiValueType, Class<?> elementType);

    /**
     * Get the source type
     *
     * @return non-null
     */
    default Class<S> getSourceType() {
        return findActualTypeArgument(getClass(), MultiValueConverter.class, 0);
    }

    /**
     * Find the {@link MultiValueConverter} instance from {@link ExtensionLoader} with the specified source and target type
     *
     * @param sourceType the source type
     * @param targetType the target type
     * @return <code>null</code> if not found
     * @see ExtensionLoader#getSupportedExtensionInstances()
     * @since 2.7.8
     */
    static MultiValueConverter<?> find(Class<?> sourceType, Class<?> targetType) {
        return getExtensionLoader(MultiValueConverter.class)
                .getSupportedExtensionInstances()
                .stream()
                .filter(converter -> converter.accept(sourceType, targetType))
                .findFirst()
                .orElse(null);
    }

    static <T> T convertIfPossible(Object source, Class<?> multiValueType, Class<?> elementType) {
        Class<?> sourceType = source.getClass();
        MultiValueConverter converter = find(sourceType, multiValueType);
        if (converter != null) {
            return (T) converter.convert(source, multiValueType, elementType);
        }
        return null;
    }
}
