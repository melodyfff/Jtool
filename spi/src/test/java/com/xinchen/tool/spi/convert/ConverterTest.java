package com.xinchen.tool.spi.convert;

import org.junit.jupiter.api.Test;

import static com.xinchen.tool.spi.convert.Converter.convertIfPossible;
import static com.xinchen.tool.spi.convert.Converter.getConverter;
import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link Converter} Test-Cases
 *
 */
public class ConverterTest {

    @Test
    public void testGetConverter() {
        getExtensionLoader(Converter.class)
                .getSupportedExtensionInstances()
                .forEach(converter -> {
                    assertSame(converter, getConverter(converter.getSourceType(), converter.getTargetType()));
                });
    }

    @Test
    public void testConvertIfPossible() {
        assertEquals(Integer.valueOf(2), convertIfPossible("2", Integer.class));
        assertEquals(Boolean.FALSE, convertIfPossible("false", Boolean.class));
        assertEquals(Double.valueOf(1), convertIfPossible("1", Double.class));
    }
}
