package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.convert.Converter;
import com.xinchen.tool.spi.convert.StringToBooleanConverter;
import com.xinchen.tool.spi.convert.StringToDoubleConverter;
import com.xinchen.tool.spi.convert.StringToIntegerConverter;
import com.xinchen.tool.spi.extension.convert.String2BooleanConverter;
import com.xinchen.tool.spi.extension.convert.String2DoubleConverter;
import com.xinchen.tool.spi.extension.convert.String2IntegerConverter;
import com.xinchen.tool.spi.extension.ext.ext1.SimpleExt;
import com.xinchen.tool.spi.extension.ext.ext1.impl.SimpleExtImpl1;
import com.xinchen.tool.spi.extension.ext.ext1.impl.SimpleExtImpl2;
import com.xinchen.tool.spi.extension.ext.ext10_multi_names.Ext10MultiNames;
import com.xinchen.tool.spi.extension.ext.ext2.Ext2;
import com.xinchen.tool.spi.extension.ext.ext6_wrap.WrappedExt;
import com.xinchen.tool.spi.extension.ext.ext6_wrap.impl.Ext5Wrapper1;
import com.xinchen.tool.spi.extension.ext.ext6_wrap.impl.Ext5Wrapper2;
import com.xinchen.tool.spi.extension.ext.ext7.InitErrorExt;
import com.xinchen.tool.spi.extension.ext.ext8_add.AddExt1;
import com.xinchen.tool.spi.extension.ext.ext8_add.AddExt2;
import com.xinchen.tool.spi.extension.ext.ext8_add.AddExt3;
import com.xinchen.tool.spi.extension.ext.ext8_add.AddExt4;
import com.xinchen.tool.spi.extension.ext.ext8_add.impl.AddExt1Impl1;
import com.xinchen.tool.spi.extension.ext.ext8_add.impl.AddExt1_ManualAdaptive;
import com.xinchen.tool.spi.extension.ext.ext8_add.impl.AddExt1_ManualAdd1;
import com.xinchen.tool.spi.extension.ext.ext8_add.impl.AddExt1_ManualAdd2;
import com.xinchen.tool.spi.extension.ext.ext8_add.impl.AddExt2_ManualAdaptive;
import com.xinchen.tool.spi.extension.ext.ext8_add.impl.AddExt3_ManualAdaptive;
import com.xinchen.tool.spi.extension.ext.ext8_add.impl.AddExt4_ManualAdaptive;
import com.xinchen.tool.spi.extension.ext.ext9_empty.Ext9Empty;
import com.xinchen.tool.spi.extension.ext.ext9_empty.impl.Ext9EmptyImpl;
import com.xinchen.tool.spi.extension.ext.injection.InjectExt;
import com.xinchen.tool.spi.extension.ext.injection.impl.InjectExtImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void test_AddExtension(){
        try {
            getExtensionLoader(AddExt1.class).getExtension("Manual1");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension com.xinchen.tool.spi.extension.ext.ext8_add.AddExt1 by name Manual"));
        }

        getExtensionLoader(AddExt1.class).addExtension("Manual1", AddExt1_ManualAdd1.class);
        AddExt1 ext = getExtensionLoader(AddExt1.class).getExtension("Manual1");

        assertThat(ext, instanceOf(AddExt1_ManualAdd1.class));
        assertEquals("Manual1", getExtensionLoader(AddExt1.class).getExtensionName(AddExt1_ManualAdd1.class));
    }

    @Test
    void test_AddExtension_NoExtend(){
        final ExtensionLoader<Ext9Empty> loader = getExtensionLoader(Ext9Empty.class);
        final Set<String> supportedExtensions = loader.getSupportedExtensions();
        assertFalse(supportedExtensions.size()>0);

        loader.addExtension("ext9", Ext9EmptyImpl.class);

        assertThat(loader.getExtension("ext9"), instanceOf(Ext9Empty.class));
        assertEquals("ext9", loader.getExtensionName(Ext9EmptyImpl.class));
    }

    @Test
    void test_AddExtension_ExceptionWhenExistedExtension(){
        try {
            getExtensionLoader(AddExt1.class).addExtension("impl1", AddExt1_ManualAdd1.class);
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Extension name impl1 already exists (Extension interface com.xinchen.tool.spi.extension.ext.ext8_add.AddExt1)!"));
        }
    }

    @Test
    void test_AddExtension_Adaptive(){
        ExtensionLoader<AddExt2> loader = getExtensionLoader(AddExt2.class);

        // 没在META-INF 里面配置默认@Adaptive，自动生成类
        // assertEquals("AddExt2$Adaptive", loader.getAdaptiveExtension().getClass().getSimpleName());

        // 这里增加的是 adpative适配器类，name字段其实没生效
        loader.addExtension(null, AddExt2_ManualAdaptive.class);

        AddExt2 adaptive = loader.getAdaptiveExtension();
        assertTrue(adaptive instanceof AddExt2_ManualAdaptive);
    }

    @Test
    void test_AddExtension_Adaptive_ExceptionWhenExistedAdaptive(){
        ExtensionLoader<AddExt1> loader = getExtensionLoader(AddExt1.class);

        // 没在META-INF 里面配置默认@Adaptive，自动生成类
         assertEquals("AddExt1$Adaptive", loader.getAdaptiveExtension().getClass().getSimpleName());

        try {
            loader.addExtension(null, AddExt1_ManualAdaptive.class);
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Adaptive Extension already exists (Extension interface com.xinchen.tool.spi.extension.ext.ext8_add.AddExt1)!"));
        }
    }

    @Test
    void test_replaceExtension(){
        try {
            getExtensionLoader(AddExt1.class).getExtension("Manual2");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension com.xinchen.tool.spi.extension.ext.ext8_add.AddExt1 by name Manual"));
        }

        {
            AddExt1 ext = getExtensionLoader(AddExt1.class).getExtension("impl1");

            assertThat(ext, instanceOf(AddExt1Impl1.class));
            assertEquals("impl1", getExtensionLoader(AddExt1.class).getExtensionName(AddExt1Impl1.class));
        }

        {
            getExtensionLoader(AddExt1.class).replaceExtension("impl1", AddExt1_ManualAdd2.class);
            AddExt1 ext = getExtensionLoader(AddExt1.class).getExtension("impl1");

            assertThat(ext, instanceOf(AddExt1_ManualAdd2.class));
            assertEquals("impl1", getExtensionLoader(AddExt1.class).getExtensionName(AddExt1_ManualAdd2.class));
        }
    }

    @Test
    void test_replaceExtension_Adaptive(){
        ExtensionLoader<AddExt3> loader = getExtensionLoader(AddExt3.class);

        AddExt3 adaptive = loader.getAdaptiveExtension();
        assertEquals("AddExt3$Adaptive", adaptive.getClass().getSimpleName());
        assertFalse(adaptive instanceof AddExt3_ManualAdaptive);

        loader.replaceExtension(null, AddExt3_ManualAdaptive.class);

        adaptive = loader.getAdaptiveExtension();
        assertTrue(adaptive instanceof AddExt3_ManualAdaptive);
    }

    @Test
    void test_replaceExtension_ExceptionWhenNotExistedExtension(){
        getExtensionLoader(AddExt1.class).getExtension("impl1");

        try {
            getExtensionLoader(AddExt1.class).replaceExtension("NotExistedExtension", AddExt1_ManualAdd1.class);
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Extension name NotExistedExtension doesn't exist (Extension interface com.xinchen.tool.spi.extension.ext.ext8_add.AddExt1)"));
        }
    }

    @Test
    void test_replaceExtension_Adaptive_ExceptionWhenNotExistedExtension(){
        ExtensionLoader<AddExt4> loader = getExtensionLoader(AddExt4.class);
        try {
            // cachedAdaptiveClass 还未加载
            loader.replaceExtension(null, AddExt4_ManualAdaptive.class);
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Adaptive Extension doesn't exist (Extension interface com.xinchen.tool.spi.extension.ext.ext8_add.AddExt4)"));
        }

        // cachedAdaptiveClass
        loader.getAdaptiveExtension();
        loader.replaceExtension(null, AddExt4_ManualAdaptive.class);

        // reload Adaptive
        AddExt4 adaptive = loader.getAdaptiveExtension();
        assertTrue(adaptive instanceof AddExt4_ManualAdaptive);
    }

    @Test
    void test_InitError(){
        ExtensionLoader<InitErrorExt> loader = getExtensionLoader(InitErrorExt.class);

        loader.getExtension("ok");

        try {
            loader.getExtension("error");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Failed to load extension class (interface: interface com.xinchen.tool.spi.extension.ext.ext7.InitErrorExt"));
            assertThat(expected.getCause(), instanceOf(ExceptionInInitializerError.class));
        }
    }

    @Test
    void testLoadActivateExtension(){

    }

    @Test
    void testLoadDefaultActivateExtension(){

    }

    @Test
    void testInjectExtension() {
        // test default
        InjectExt injectExt = getExtensionLoader(InjectExt.class).getExtension("injection");

        InjectExtImpl injectExtImpl = (InjectExtImpl) injectExt;

        Assertions.assertNotNull(injectExtImpl.getSimpleExt());

        // @DisableInject 标记
        Assertions.assertNull(injectExtImpl.getSimpleExt1());

        // 常规类型在 spi extension里面没有找到
        Assertions.assertNull(injectExtImpl.getGenericType());
    }

    @Test
    void testMultiNames() {
        Ext10MultiNames ext10MultiNames = getExtensionLoader(Ext10MultiNames.class).getExtension("impl");
        assertNotNull(ext10MultiNames);
        ext10MultiNames = getExtensionLoader(Ext10MultiNames.class).getExtension("implMultiName");
        assertNotNull(ext10MultiNames);

        assertEquals(getExtensionLoader(Ext10MultiNames.class).getExtension("impl"),
                getExtensionLoader(Ext10MultiNames.class).getExtension("implMultiName"));

        assertThrows(
                IllegalStateException.class,
                () -> getExtensionLoader(Ext10MultiNames.class).getExtension("impl,implMultiName")
        );
    }

    @Test
    void testGetOrDefaultExtension(){
        ExtensionLoader<InjectExt> loader = getExtensionLoader(InjectExt.class);

        // 返回默认的extension
        InjectExt injectExt = loader.getDefaultExtension("non-exists");

        assertEquals(InjectExtImpl.class, injectExt.getClass());
        assertEquals(InjectExtImpl.class, loader.getOrDefaultExtension("injection").getClass());
    }

    @Test
    void testGetSupported() {
        ExtensionLoader<InjectExt> loader = getExtensionLoader(InjectExt.class);
        assertEquals(1, loader.getSupportedExtensions().size());
        assertEquals(Collections.singleton("injection"), loader.getSupportedExtensions());
    }

    @Test
    void testOverridden() {
        ExtensionLoader<Converter> loader = getExtensionLoader(Converter.class);

        // LoadingStrategyApp和自定义加载策略-LoadingStrategyAppCustomize优先级低于内部internal的，再加载的时候，允许覆盖
        Converter converter = loader.getExtension("string-to-boolean");
        assertEquals(String2BooleanConverter.class, converter.getClass());


        converter = loader.getExtension("string-to-double");
        assertEquals(String2DoubleConverter.class, converter.getClass());

        converter = loader.getExtension("string-to-integer");
        assertEquals(String2IntegerConverter.class, converter.getClass());


        // cachedInstances中存储的值已经被cover
        // ConcurrentMap<Class<?>, String> cachedNames 中还存有对应关系
        assertEquals("string-to-boolean", loader.getExtensionName(String2BooleanConverter.class));
        assertEquals("string-to-boolean", loader.getExtensionName(StringToBooleanConverter.class));

        assertEquals("string-to-double", loader.getExtensionName(String2DoubleConverter.class));
        assertEquals("string-to-double", loader.getExtensionName(StringToDoubleConverter.class));

        assertEquals("string-to-integer", loader.getExtensionName(String2IntegerConverter.class));
        assertEquals("string-to-integer", loader.getExtensionName(StringToIntegerConverter.class));
    }
}