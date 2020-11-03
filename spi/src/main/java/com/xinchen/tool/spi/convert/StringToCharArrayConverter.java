package com.xinchen.tool.spi.convert;


import static com.xinchen.tool.spi.utils.StringUtils.isNotEmpty;

/**
 * The class to convert {@link String} to <code>char[]</code>
 *
 */
public class StringToCharArrayConverter implements StringConverter<char[]> {

    @Override
    public char[] convert(String source) {
        return isNotEmpty(source) ? source.toCharArray() : null;
    }


    @Override
    public int getPriority() {
        return NORMAL_PRIORITY + 7;
    }
}
