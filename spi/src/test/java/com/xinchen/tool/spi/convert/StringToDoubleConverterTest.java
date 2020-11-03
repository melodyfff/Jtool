package com.xinchen.tool.spi.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link StringToDoubleConverter} Test
 *
 */
public class StringToDoubleConverterTest {

    private StringToDoubleConverter converter;

    @BeforeEach
    public void init() {
        converter = (StringToDoubleConverter) getExtensionLoader(Converter.class).getExtension("string-to-double");
    }

    @Test
    public void testAccept() {
        assertTrue(converter.accept(String.class, Double.class));
    }

    @Test
    public void testConvert() {
        assertEquals(Double.valueOf("1.0"), converter.convert("1.0"));
        assertNull(converter.convert(null));
        assertThrows(NumberFormatException.class, () -> {
            converter.convert("ttt");
        });
    }
}
