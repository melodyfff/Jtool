package com.xinchen.tool.spi.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link StringToBooleanConverter} Test
 *
 * @since 2.7.6
 */
public class StringToBooleanConverterTest {

    private StringToBooleanConverter converter;

    @BeforeEach
    public void init() {
        converter = (StringToBooleanConverter) getExtensionLoader(Converter.class).getExtension("string-to-boolean");
    }

    @Test
    public void testAccept() {
        assertTrue(converter.accept(String.class, Boolean.class));
    }

    @Test
    public void testConvert() {
        assertTrue(converter.convert("true"));
        assertTrue(converter.convert("true"));
        assertTrue(converter.convert("True"));
        assertFalse(converter.convert("a"));
        assertNull(converter.convert(""));
        assertNull(converter.convert(null));
    }
}
