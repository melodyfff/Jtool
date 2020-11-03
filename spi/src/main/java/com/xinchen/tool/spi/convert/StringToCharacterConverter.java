package com.xinchen.tool.spi.convert;


import static com.xinchen.tool.spi.utils.StringUtils.length;

/**
 * The class to convert {@link String} to {@link Character}
 *
 * @since 2.7.6
 */
public class StringToCharacterConverter implements StringConverter<Character> {

    @Override
    public Character convert(String source) {
        int length = length(source);
        if (length == 0) {
            return null;
        }
        if (length > 1) {
            throw new IllegalArgumentException("The source String is more than one character!");
        }
        return source.charAt(0);
    }

    @Override
    public int getPriority() {
        return NORMAL_PRIORITY + 8;
    }
}
