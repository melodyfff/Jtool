package com.xinchen.tool.spi.convert;

import static com.xinchen.tool.spi.utils.StringUtils.isNotEmpty;
import static java.lang.Integer.valueOf;

/**
 * The class to convert {@link String} to {@link Integer}
 *
 * @since 2.7.6
 */
public class StringToIntegerConverter implements StringConverter<Integer> {

    @Override
    public Integer convert(String source) {
        return isNotEmpty(source) ? valueOf(source) : null;
    }

    @Override
    public int getPriority() {
        return NORMAL_PRIORITY;
    }
}
