package com.xinchen.tool.spi.convert.multiple;


import com.xinchen.tool.spi.utils.ArrayUtils;

import static com.xinchen.tool.spi.utils.StringUtils.isEmpty;
import static com.xinchen.tool.spi.utils.StringUtils.split;

/**
 * The class to convert {@link String} to multiple value object
 *
 * @see MultiValueConverter
 */
public interface StringToMultiValueConverter extends MultiValueConverter<String> {

    @Override
    default Object convert(String source, Class<?> multiValueType, Class<?> elementType) {

        if (isEmpty(source)) {
            return null;
        }

        // split by the comma
        String[] segments = split(source, ',');

        // If empty array, create an array with only one element
        if (ArrayUtils.isEmpty(segments)) {
            segments = new String[]{source};
        }

        int size = segments.length;

        return convert(segments, size, multiValueType, elementType);
    }

    /**
     * Convert the segments to multiple value object
     *
     * @param segments    the String array of content
     * @param size        the size of multiple value object
     * @param targetType  the target type
     * @param elementType the element type
     * @return multiple value object
     */
    Object convert(String[] segments, int size, Class<?> targetType, Class<?> elementType);
}
