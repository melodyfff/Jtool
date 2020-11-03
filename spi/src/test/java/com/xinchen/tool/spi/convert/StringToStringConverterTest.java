package com.xinchen.tool.spi.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link StringToStringConverter} Test
 *
 */
public class StringToStringConverterTest {

    private StringToStringConverter converter;

    @BeforeEach
    public void init() {
        converter = (StringToStringConverter) getExtensionLoader(Converter.class).getExtension("string-to-string");
    }

    @Test
    public void testAccept() {
        assertTrue(converter.accept(String.class, String.class));
    }

    @Test
    public void testConvert() {
        assertEquals("1", converter.convert("1"));
        assertNull(converter.convert(null));
    }
}
