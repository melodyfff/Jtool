package com.xinchen.tool.spi.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link StringToIntegerConverter} Test
 *
 */
public class StringToIntegerConverterTest {

    private StringToIntegerConverter converter;

    @BeforeEach
    public void init() {
        converter = (StringToIntegerConverter) getExtensionLoader(Converter.class).getExtension("string-to-integer");
    }

    @Test
    public void testAccept() {
        assertTrue(converter.accept(String.class, Integer.class));
    }

    @Test
    public void testConvert() {
        assertEquals(Integer.valueOf("1"), converter.convert("1"));
        assertNull(converter.convert(null));
        assertThrows(NumberFormatException.class, () -> {
            converter.convert("ttt");
        });
    }
}
