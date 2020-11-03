package com.xinchen.tool.spi.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link StringToCharArrayConverter} Test
 *
 */
public class StringToCharArrayConverterTest {

    private StringToCharArrayConverter converter;

    @BeforeEach
    public void init() {
        converter =  (StringToCharArrayConverter) getExtensionLoader(Converter.class).getExtension("string-to-char-array");
    }

    @Test
    public void testAccept() {
        assertTrue(converter.accept(String.class, char[].class));
    }

    @Test
    public void testConvert() {
        assertArrayEquals(new char[]{'1', '2', '3'}, converter.convert("123"));
        assertNull(converter.convert(null));
    }
}
