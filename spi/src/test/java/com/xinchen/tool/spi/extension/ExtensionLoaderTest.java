package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.extension.ext.ext1.SimpleExt;
import com.xinchen.tool.spi.extension.ext.ext1.impl.SimpleExtImpl1;
import org.junit.jupiter.api.Test;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/11/1 0:47
 */
public class ExtensionLoaderTest {
    @Test
    public void test_getExtensionLoader_Type_Null() throws Exception {
        try {
            getExtensionLoader(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension type == null"));
        }
    }

    @Test
    public void test_getExtensionLoader_NotInterface() throws Exception {
        try {
            getExtensionLoader(ExtensionLoaderTest.class);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension type (class com.xinchen.tool.spi.extension.ExtensionLoaderTest) is not an interface"));
        }
    }

    @Test
    public void test_getExtensionLoader_NotSpiAnnotation() throws Exception {
        try {
            getExtensionLoader(NoSpiExt.class);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(),
                    allOf(containsString("com.xinchen.tool.spi.extension.NoSpiExt"),
                            containsString("is not an extension"),
                            containsString("NOT annotated with @SPI")));
        }
    }

    @Test
    public void test_getDefaultExtension() throws Exception {
        SimpleExt ext = getExtensionLoader(SimpleExt.class).getDefaultExtension();
        assertThat(ext, instanceOf(SimpleExtImpl1.class));

        String name = getExtensionLoader(SimpleExt.class).getDefaultExtensionName();
        assertEquals("impl1", name);
    }
}
