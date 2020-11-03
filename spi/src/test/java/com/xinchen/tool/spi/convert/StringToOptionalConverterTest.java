package com.xinchen.tool.spi.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link StringToOptionalConverter} Test
 *
 */
public class StringToOptionalConverterTest {

    private StringToOptionalConverter converter;

    @BeforeEach
    public void init() {
        converter = (StringToOptionalConverter) getExtensionLoader(Converter.class).getExtension("string-to-optional");
    }

    @Test
    public void testAccept() {
        assertTrue(converter.accept(String.class, Optional.class));
    }

    @Test
    public void testConvert() {
        assertEquals(Optional.of("1"), converter.convert("1"));
        assertEquals(Optional.empty(), converter.convert(null));
    }
}
