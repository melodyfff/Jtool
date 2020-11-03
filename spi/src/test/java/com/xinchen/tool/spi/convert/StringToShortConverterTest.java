package com.xinchen.tool.spi.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link StringToShortConverter} Test
 *
 */
public class StringToShortConverterTest {

    private StringToShortConverter converter;

    @BeforeEach
    public void init() {
        converter = (StringToShortConverter) getExtensionLoader(Converter.class).getExtension("string-to-short");
    }

    @Test
    public void testAccept() {
        assertTrue(converter.accept(String.class, Short.class));
    }

    @Test
    public void testConvert() {
        assertEquals(Short.valueOf("1"), converter.convert("1"));
        assertNull(converter.convert(null));
        assertThrows(NumberFormatException.class, () -> {
            converter.convert("ttt");
        });
    }
}
