package com.xinchen.tool.spi.convert;

import com.xinchen.tool.spi.extension.ExtensionLoader;
import com.xinchen.tool.spi.extension.SPI;
import com.xinchen.tool.spi.lang.Prioritized;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static com.xinchen.tool.spi.utils.ClassUtils.isAssignableFrom;
import static com.xinchen.tool.spi.utils.TypeUtils.findActualTypeArgument;

/**
 *
 * A class to convert the source-typed value to the target-typed value
 *
 * @param <S> The source type
 * @param <T> The target type
 *
 * @author xinchen
 * @version 1.0
 * @date 03/11/2020 13:40
 */
@SPI
@FunctionalInterface
public interface Converter<S,T> extends Prioritized {

    /**
     * Convert the source-typed value to the target-typed value
     *
     * @param source the source-typed value
     * @return the target-typed value
     */
    T convert(S source);

    /**
     * Accept the source type and target type or not
     *
     * @param sourceType the source type
     * @param targetType the target type
     * @return if accepted, return <code>true</code>, or <code>false</code>
     */
    default boolean accept(Class<?> sourceType, Class<?> targetType){
        return isAssignableFrom(sourceType, getSourceType()) && isAssignableFrom(targetType, getTargetType());
    }

    /**
     * Get the source type
     *
     * @return non-null
     */
    default Class<S> getSourceType() {
        return findActualTypeArgument(getClass(), Converter.class, 0);
    }

    /**
     * Get the target type
     *
     * @return non-null
     */
    default Class<T> getTargetType() {
        return findActualTypeArgument(getClass(), Converter.class, 1);
    }

    /**
     * Get the Converter instance from {@link ExtensionLoader} with the specified source and target type
     *
     * @param sourceType the source type
     * @param targetType the target type
     * @return Converter
     * @see ExtensionLoader#getSupportedExtensionInstances()
     */
    static Converter<?, ?> getConverter(Class<?> sourceType, Class<?> targetType) {
        return getExtensionLoader(Converter.class)
                .getSupportedExtensionInstances()
                .stream()
                .filter(converter -> converter.accept(sourceType, targetType))
                .findFirst()
                .orElse(null);
    }

    /**
     * Convert the value of source to target-type value if possible
     *
     * @param source     the value of source
     * @param targetType the target type
     * @param <T>        the target type
     * @return <code>null</code> if can't be converted
     */
    static <T> T convertIfPossible(Object source, Class<T> targetType) {
        Converter converter = getConverter(source.getClass(), targetType);
        if (converter != null) {
            return (T) converter.convert(source);
        }
        return null;
    }
}
