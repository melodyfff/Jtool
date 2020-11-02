package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.ext.ext1.SimpleExt;
import com.xinchen.tool.spi.extension.ext.ext1.impl.SimpleExtImpl1;
import com.xinchen.tool.spi.extension.ext.ext1.impl.SimpleExtImpl2;
import com.xinchen.tool.spi.extension.ext.ext2.Ext2;
import com.xinchen.tool.spi.extension.ext.ext6_wrap.WrappedExt;
import com.xinchen.tool.spi.extension.ext.ext6_wrap.impl.Ext5Wrapper1;
import com.xinchen.tool.spi.extension.ext.ext6_wrap.impl.Ext5Wrapper2;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/11/1 0:47
 */
class ExtensionLoaderTest {
    @Test
    void test_getExtensionLoader_Type_Null()  {
        try {
            getExtensionLoader(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension type == null"));
        }
    }

    @Test
    void test_getExtensionLoader_NotInterface()  {
        try {
            getExtensionLoader(ExtensionLoaderTest.class);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension type (class com.xinchen.tool.spi.extension.ExtensionLoaderTest) is not an interface"));
        }
    }

    @Test
    void test_getExtensionLoader_NotSpiAnnotation()  {
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
    void test_getDefaultExtension() {
        SimpleExt ext = getExtensionLoader(SimpleExt.class).getDefaultExtension();
        assertThat(ext, instanceOf(SimpleExtImpl1.class));

        String name = getExtensionLoader(SimpleExt.class).getDefaultExtensionName();
        assertEquals("impl1", name);
    }

    @Test
    void test_getDefaultExtension_NULL(){
        // @SPI没指定默认的Value
        Ext2 ext = getExtensionLoader(Ext2.class).getDefaultExtension();
        assertNull(ext);

        String name = getExtensionLoader(Ext2.class).getDefaultExtensionName();
        assertNull(name);
    }


    @Test
    void test_getExtension(){
        assertTrue(getExtensionLoader(SimpleExt.class).getExtension("impl1") instanceof SimpleExtImpl1);
        assertTrue(getExtensionLoader(SimpleExt.class).getExtension("impl2") instanceof SimpleExtImpl2);
    }

    @Test
    void test_getExtension_WithWrapper(){
        // 由于没有指定@Aactive#order 可能返回:
        // 1. Ext5Wrapper1 -> Ext5Wrapper2 -> instance
        // 2. Ext5Wrapper2 -> Ext5Wrapper1 -> instance
        WrappedExt impl1 = getExtensionLoader(WrappedExt.class).getExtension("impl1");
        assertThat(impl1, anyOf(instanceOf(Ext5Wrapper1.class), instanceOf(Ext5Wrapper2.class)));

        WrappedExt impl2 = getExtensionLoader(WrappedExt.class).getExtension("impl2");
        assertThat(impl2, anyOf(instanceOf(Ext5Wrapper1.class), instanceOf(Ext5Wrapper2.class)));

        URL url = URL.valueOf("p1://1.2.3.4:1010/path1");

        int echoCount1 = Ext5Wrapper1.echoCount.get();
        int echoCount2 = Ext5Wrapper2.echoCount.get();

        // 嵌套包裹，计数器都增加
        assertEquals("Ext5Impl1-echo", impl1.echo(url, "ha"));
        assertEquals("Ext5Impl2-echo", impl2.echo(url, "ha"));
        assertEquals(echoCount1 + 2, Ext5Wrapper1.echoCount.get());
        assertEquals(echoCount2 + 2, Ext5Wrapper2.echoCount.get());

    }

    @Test
    void test_getExtension_ExceptionNoExtension(){
        try {
            getExtensionLoader(SimpleExt.class).getExtension("XXX");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension com.xinchen.tool.spi.extension.ext.ext1.SimpleExt by name XXX"));
        }
    }

    @Test
    void test_getExtension_ExceptionNoExtension_WrapperNotEffectiveName() {
        try {
            getExtensionLoader(WrappedExt.class).getExtension("XXX");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension com.xinchen.tool.spi.extension.ext.ext6_wrap.WrappedExt by name XXX"));
        }
    }

    @Test
    void test_getExtension_ExceptionNullArg() {
        try {
            getExtensionLoader(SimpleExt.class).getExtension(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension name == null"));
        }
    }

    @Test
    void test_hasExtension() {
        assertTrue(getExtensionLoader(SimpleExt.class).hasExtension("impl1"));
        // 从cachedClasses中获取, ','分隔符不起作用
        assertFalse(getExtensionLoader(SimpleExt.class).hasExtension("impl1,impl2"));
        assertFalse(getExtensionLoader(SimpleExt.class).hasExtension("xxx"));

        try {
            getExtensionLoader(SimpleExt.class).hasExtension(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension name == null"));
        }
    }

    @Test
    void test_hasExtension_wrapperIsNotExt(){
        assertTrue(getExtensionLoader(WrappedExt.class).hasExtension("impl1"));
        assertFalse(getExtensionLoader(WrappedExt.class).hasExtension("impl1,impl2"));
        assertFalse(getExtensionLoader(WrappedExt.class).hasExtension("xxx"));

        // cachedClasses中不会存储Wrap的class , 会存在cachedWrapperClasses中
        assertFalse(getExtensionLoader(WrappedExt.class).hasExtension("wrapper1"));

        try {
            getExtensionLoader(WrappedExt.class).hasExtension(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension name == null"));
        }
    }

    @Test
    void test_getSupportedExtensions() {
        Set<String> exts = getExtensionLoader(SimpleExt.class).getSupportedExtensions();

        Set<String> expected = new HashSet<String>();
        expected.add("impl1");
        expected.add("impl2");
        expected.add("impl3");

        assertEquals(expected, exts);
    }

    @Test
    void test_getSupportedExtensions_wrapperIsNotExt() {
        Set<String> exts = getExtensionLoader(WrappedExt.class).getSupportedExtensions();

        Set<String> expected = new HashSet<String>();
        expected.add("impl1");
        expected.add("impl2");

        assertEquals(expected, exts);
    }
}