package com.xinchen.tool.spi.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link StringToLongConverter} Test
 *
 * @since 2.7.6
 */
public class StringToLongConverterTest {

    private StringToLongConverter converter;

    @BeforeEach
    public void init() {
        converter = (StringToLongConverter) getExtensionLoader(Converter.class).getExtension("string-to-long");
    }

    @Test
    public void testAccept() {
        assertTrue(converter.accept(String.class, Long.class));
    }

    @Test
    public void testConvert() {
        assertEquals(Long.valueOf("1"), converter.convert("1"));
        assertNull(converter.convert(null));
        assertThrows(NumberFormatException.class, () -> {
            converter.convert("ttt");
        });
    }
}
