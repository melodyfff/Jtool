package com.xinchen.tool.spi.convert;

import static com.xinchen.tool.spi.utils.StringUtils.isNotEmpty;
import static java.lang.Float.valueOf;

/**
 * The class to convert {@link String} to {@link Float}
 *
 * @since 2.7.6
 */
public class StringToFloatConverter implements StringConverter<Float> {

    @Override
    public Float convert(String source) {
        return isNotEmpty(source) ? valueOf(source) : null;
    }

    @Override
    public int getPriority() {
        return NORMAL_PRIORITY + 4;
    }
}
