package com.xinchen.tool.spi.convert;

import static com.xinchen.tool.spi.utils.StringUtils.isNotEmpty;

/**
 * The class to convert {@link String} to {@link Short}
 *
 * @since 2.7.6
 */
public class StringToShortConverter implements StringConverter<Short> {

    @Override
    public Short convert(String source) {
        return isNotEmpty(source) ? Short.valueOf(source) : null;
    }


    @Override
    public int getPriority() {
        return NORMAL_PRIORITY + 2;
    }
}
