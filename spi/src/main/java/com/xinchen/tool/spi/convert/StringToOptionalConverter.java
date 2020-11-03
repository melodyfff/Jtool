package com.xinchen.tool.spi.convert;

import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * The class to convert {@link String} to {@link Optional}
 *
 */
public class StringToOptionalConverter implements StringConverter<Optional> {

    @Override
    public Optional convert(String source) {
        return ofNullable(source);
    }


    @Override
    public int getPriority() {
        return MIN_PRIORITY;
    }
}
