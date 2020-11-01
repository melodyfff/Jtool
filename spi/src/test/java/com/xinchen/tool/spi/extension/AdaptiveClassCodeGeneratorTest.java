package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.extension.adaptive.AppAdaptiveExt;
import com.xinchen.tool.spi.extension.ext.ext1.SimpleExt;
import com.xinchen.tool.spi.utlis.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 * {@link AdaptiveClassCodeGenerator} Test
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 15:55
 */
public class AdaptiveClassCodeGeneratorTest {

    @Test
    public void generate() throws IOException {
        final AdaptiveClassCodeGenerator generator = new AdaptiveClassCodeGenerator(AppAdaptiveExt.class, "impl");
        final String value = generator.generate();
        URL url = getClass().getResource("/com/xinchen/tool/spi/extension/AppAdaptiveExt$Adaptive");
        try (InputStream inputStream = url.openStream()){
            String content = IOUtils.read(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            // in Windows platform content get from resource contains \r delimiter
            content = content.replaceAll("\r","");
            assertTrue(content.contains(value));
        }
        System.out.println(value);
    }
//
//    @Test
//    public void generate2() throws IOException {
//        final AdaptiveClassCodeGenerator generator = new AdaptiveClassCodeGenerator(SimpleExt.class, "impl1");
//        final String value = generator.generate();
//        URL url = getClass().getResource("/com/xinchen/tool/spi/extension/SimpleExt$Adaptive");
//        try (InputStream inputStream = url.openStream()){
//            String content = IOUtils.read(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//            // in Windows platform content get from resource contains \r delimiter
//            content = content.replaceAll("\r","");
//            assertTrue(content.contains(value.trim()));
//        }
//        System.out.println(value);
//    }
}