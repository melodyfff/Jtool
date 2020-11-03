package com.xinchen.tool.spi.convert;

import static com.xinchen.tool.spi.utils.StringUtils.isNotEmpty;
import static java.lang.Double.valueOf;

/**
 * The class to convert {@link String} to {@link Double}
 *
 */
public class StringToDoubleConverter implements StringConverter<Double> {

    @Override
    public Double convert(String source) {
        return isNotEmpty(source) ? valueOf(source) : null;
    }


    @Override
    public int getPriority() {
        return NORMAL_PRIORITY + 3;
    }
}
