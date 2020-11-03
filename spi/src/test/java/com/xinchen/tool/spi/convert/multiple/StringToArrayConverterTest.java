package com.xinchen.tool.spi.convert.multiple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Objects.deepEquals;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link StringToArrayConverter} Test
 *
 */
public class StringToArrayConverterTest {

    private StringToArrayConverter converter;

    @BeforeEach
    public void init() {
        converter = new StringToArrayConverter();
    }

    @Test
    public void testAccept() {
        assertTrue(converter.accept(String.class, char[].class));
        assertTrue(converter.accept(null, char[].class));
        assertFalse(converter.accept(null, String.class));
        assertFalse(converter.accept(null, String.class));
        assertFalse(converter.accept(null, null));
    }

    @Test
    public void testConvert() {
        assertTrue(deepEquals(new Integer[]{123}, converter.convert("123", Integer[].class, Integer.class)));
        assertTrue(deepEquals(new Integer[]{1, 2, 3}, converter.convert("1,2,3", Integer[].class, null)));
        assertNull(converter.convert("", Integer[].class, null));
        assertNull(converter.convert(null, Integer[].class, null));
    }

    @Test
    public void testGetSourceType() {
        assertEquals(String.class, converter.getSourceType());
    }

    @Test
    public void testGetPriority() {
        assertEquals(Integer.MAX_VALUE, converter.getPriority());
    }
}
