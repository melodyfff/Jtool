package com.xinchen.tool.spi.convert;

import static com.xinchen.tool.spi.utils.StringUtils.isNotEmpty;
import static java.lang.Long.valueOf;

/**
 * The class to convert {@link String} to {@link Long}
 *
 */
public class StringToLongConverter implements StringConverter<Long> {

    @Override
    public Long convert(String source) {
        return isNotEmpty(source) ? valueOf(source) : null;
    }


    @Override
    public int getPriority() {
        return NORMAL_PRIORITY + 1;
    }
}
