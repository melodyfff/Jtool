package com.xinchen.tool.spi.convert;


import static com.xinchen.tool.spi.utils.StringUtils.isNotEmpty;
import static java.lang.Boolean.valueOf;

/**
 * The class to convert {@link String} to {@link Boolean}
 *
 * @since 2.7.6
 */
public class StringToBooleanConverter implements StringConverter<Boolean> {

    @Override
    public Boolean convert(String source) {
        return isNotEmpty(source) ? valueOf(source) : null;
    }

    @Override
    public int getPriority() {
        return NORMAL_PRIORITY + 5;
    }
}
