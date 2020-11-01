package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.adaptive.AppAdaptiveExt;
import com.xinchen.tool.spi.extension.adaptive.impl.AppAdaptiveExt_Manual_Adaptive;
import com.xinchen.tool.spi.extension.ext.ext1.SimpleExt;
import com.xinchen.tool.spi.extension.ext.ext2.Ext2;
import com.xinchen.tool.spi.extension.ext.ext2.UrlHolder;
import com.xinchen.tool.spi.extension.ext.ext4.NoUrlParamExt;
import com.xinchen.tool.spi.extension.ext.ext6_inject.Ext6;
import com.xinchen.tool.spi.extension.ext.ext6_inject.impl.Ext6Impl2;
import com.xinchen.tool.spi.utils.LogUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 17:42
 */
public class ExtensionLoader_Adaptive_Test {
    @Test
    public void test_use_AdaptiveClass(){
        // Class上有@Adaptive标记的适配器类优先级 > @SPI接口下的Method上的@Adaptive

        // adaptive

        ExtensionLoader<AppAdaptiveExt> loader = ExtensionLoader.getExtensionLoader(AppAdaptiveExt.class);
        AppAdaptiveExt adaptiveExtension = loader.getAdaptiveExtension();

        {
            assertTrue(adaptiveExtension instanceof AppAdaptiveExt_Manual_Adaptive);
        }

        {
            Map<String, String> map = new HashMap<String, String>();
            map.put("key", "impl");
            URL url = new URL("p1", "1.2.3.4", 1010, "path1", map);
            assertEquals("AppAdaptiveExtImpl", adaptiveExtension.echo(url, ""));
        }

        {
            Map<String, String> map = new HashMap<String, String>();
            map.put("key", "adaptive");
            URL url = new URL("p1", "1.2.3.4", 1010, "path1", map);
            try {
                adaptiveExtension.echo(url, "");
                fail();
            } catch (IllegalStateException e){
                assertThat(e.getMessage(),
                        containsString("No such extension com.xinchen.tool.spi.extension.adaptive.AppAdaptiveExt by name adaptive"));
            }
        }
    }

    @Test
    public void test_getAdaptiveExtension_defaultAdaptiveKey()  {
        // SimpleExt的子类实现中没有@Adaptive,所以以下代码都是Compiler生成的
        // @SPI("impl1")
        {
            SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getAdaptiveExtension();
            Map<String, String> map = new HashMap<String, String>();
            //   p1://1.2.3.4:1010/path1
            URL url = new URL("p1", "1.2.3.4", 1010, "path1", map);

            String echo = ext.echo(url, "haha");
            assertEquals("Ext1Impl1-echo", echo);
        }

        {
            SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getAdaptiveExtension();

            Map<String, String> map = new HashMap<String, String>();
            // 在Compiler生成的Class中,
            map.put("simple.ext", "impl2");
            // p1://1.2.3.4:1010/path1?simple.ext=impl2
            URL url = new URL("p1", "1.2.3.4", 1010, "path1", map);

            String echo = ext.echo(url, "haha");
            assertEquals("Ext1Impl2-echo", echo);
        }

    }

    @Test
    public void test_getAdaptiveExtension_no_Adaptive_Method(){
        try {
            SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getAdaptiveExtension();
            ext.bang(null, 0);
            fail();
        } catch (UnsupportedOperationException e){
            assertThat(e.getMessage(),
                    allOf(
                            containsString("com.xinchen.tool.spi.extension.ext.ext1.SimpleExt.bang(com.xinchen.tool.spi.URL,int)"),
                            containsString("is not adaptive method!")
                    )
            );
        }
    }

    @Test
    public void test_getAdaptiveExtension_customizeAdaptiveKey()  {
        SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getAdaptiveExtension();

        Map<String, String> map = new HashMap<String, String>();
        map.put("key2", "impl2");
        URL url = new URL("p1", "1.2.3.4", 1010, "path1", map);

        String echo = ext.yell(url, "haha");
        assertEquals("Ext1Impl2-yell", echo);

        // note: URL is value's type
        // p1://1.2.3.4:1010/path1?key1=impl3&key2=impl2
        url = url.addParameter("key1", "impl3");
        echo = ext.yell(url, "haha");
        assertEquals("Ext1Impl3-yell", echo);
    }

    @Test
    public void test_getAdaptiveExtension_ExceptionWhenNoUrlAttribute()  {
        try {
            ExtensionLoader.getExtensionLoader(NoUrlParamExt.class).getAdaptiveExtension();
            fail();
        } catch (Exception expected) {
            assertThat(expected.getMessage(), containsString("Failed to create adaptive class for interface "));
            assertThat(expected.getMessage(), containsString(": not found url parameter or url attribute in parameters of method "));
        }
    }

    @Test
    public void test_urlHolder_getAdaptiveExtension()  {
        Ext2 ext = ExtensionLoader.getExtensionLoader(Ext2.class).getAdaptiveExtension();

        Map<String, String> map = new HashMap<String, String>();
        map.put("ext2", "impl1");
        URL url = new URL("p1", "1.2.3.4", 1010, "path1", map);

        UrlHolder holder = new UrlHolder();
        holder.setUrl(url);

        String echo = ext.echo(holder, "haha");
        assertEquals("Ext2Impl1-echo", echo);
    }

    @Test
    public void test_urlHolder_getAdaptiveExtension_noExtension() {
        Ext2 ext = ExtensionLoader.getExtensionLoader(Ext2.class).getAdaptiveExtension();

        URL url = new URL("p1", "1.2.3.4", 1010, "path1");

        UrlHolder holder = new UrlHolder();
        holder.setUrl(url);

        try {
            ext.echo(holder, "haha");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Failed to get extension"));
        }

        url = url.addParameter("ext2", "XXX");
        holder.setUrl(url);
        try {
            ext.echo(holder, "haha");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension"));
        }
    }

    @Test
    public void test_urlHolder_getAdaptiveExtension_UrlNpe(){
        Ext2 ext = ExtensionLoader.getExtensionLoader(Ext2.class).getAdaptiveExtension();

        try {
            ext.echo(null, "haha");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("com.xinchen.tool.spi.extension.ext.ext2.UrlHolder argument == null", e.getMessage());
        }

        try {
            ext.echo(new UrlHolder(), "haha");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("com.xinchen.tool.spi.extension.ext.ext2.UrlHolder argument getUrl() == null", e.getMessage());
        }
    }

    @Test
    public void test_urlHolder_getAdaptiveExtension_ExceptionWhenNameNotProvided() {
        Ext2 ext = ExtensionLoader.getExtensionLoader(Ext2.class).getAdaptiveExtension();

        URL url = new URL("p1", "1.2.3.4", 1010, "path1");

        UrlHolder holder = new UrlHolder();
        holder.setUrl(url);

        try {
            ext.echo(holder, "impl1");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Failed to get extension"));
        }

        url = url.addParameter("key1", "impl1");
        holder.setUrl(url);
        try {
            ext.echo(holder, "haha");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Failed to get extension (com.xinchen.tool.spi.extension.ext.ext2.Ext2) name from url"));
        }

        // 这里的key为 Compiler 生成
        url = url.addParameter("ext2", "impl2");
        holder.setUrl(url);
        String echo = ext.echo(holder, "haha");
        assertEquals("Ext2Impl2-echo", echo);
    }

    @Test
    public void test_getAdaptiveExtension_inject() {
        // Ext6Impl1 依赖 SimpleExt
        LogUtil.start();
        Ext6 ext = ExtensionLoader.getExtensionLoader(Ext6.class).getAdaptiveExtension();

        URL url = new URL("p1", "1.2.3.4", 1010, "path1");
        url = url.addParameters("ext6", "impl1");

        // IOC注入依赖 Ext1Impl1 默认值
        assertEquals("Ext6Impl1-echo-Ext1Impl1-echo", ext.echo(url, "ha"));

        Assertions.assertTrue(LogUtil.checkNoError(), "can not find error.");
        LogUtil.stop();

        // IOC注入依赖 指定值 Ext1Impl2 默认值
        url = url.addParameters("simple.ext", "impl2");
        assertEquals("Ext6Impl1-echo-Ext1Impl2-echo", ext.echo(url, "ha"));
    }

    @Test
    public void test_getAdaptiveExtension_InjectNotExtFail() {
        Ext6 ext = ExtensionLoader.getExtensionLoader(Ext6.class).getExtension("impl2");

        Ext6Impl2 impl = (Ext6Impl2) ext;
        assertNull(impl.getList());
    }
}
