package com.xinchen.tool.spi.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link StringToFloatConverter} Test
 *
 * @since 2.7.6
 */
public class StringToFloatConverterTest {

    private StringToFloatConverter converter;

    @BeforeEach
    public void init() {
        converter = (StringToFloatConverter) getExtensionLoader(Converter.class).getExtension("string-to-float");
    }

    @Test
    public void testAccept() {
        assertTrue(converter.accept(String.class, Float.class));
    }

    @Test
    public void testConvert() {
        assertEquals(Float.valueOf("1.0"), converter.convert("1.0"));
        assertNull(converter.convert(null));
        assertThrows(NumberFormatException.class, () -> {
            converter.convert("ttt");
        });
    }
}
