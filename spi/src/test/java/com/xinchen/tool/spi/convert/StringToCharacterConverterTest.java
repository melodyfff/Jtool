package com.xinchen.tool.spi.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link StringToCharacterConverter} Test
 *
 */
public class StringToCharacterConverterTest {

    private StringToCharacterConverter converter;

    @BeforeEach
    public void init() {
        converter = (StringToCharacterConverter) getExtensionLoader(Converter.class).getExtension("string-to-character");
    }

    @Test
    public void testAccept() {
        assertTrue(converter.accept(String.class, Character.class));
    }

    @Test
    public void testConvert() {
        assertEquals('t', converter.convert("t"));
        assertNull(converter.convert(null));
        assertThrows(IllegalArgumentException.class, () -> {
            converter.convert("ttt");
        });
    }
}
