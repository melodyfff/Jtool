package com.xinchen.tool.spi.convert;

/**
 * A class to covert {@link String} to {@link String} value, just no-op
 *
 */
public class StringToStringConverter implements StringConverter<String> {

    @Override
    public String convert(String source) {
        return source;
    }
}
