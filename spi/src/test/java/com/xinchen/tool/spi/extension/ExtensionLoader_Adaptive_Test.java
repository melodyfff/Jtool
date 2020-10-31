package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.extension.adaptive.AppAdaptiveExt;
import com.xinchen.tool.spi.extension.adaptive.impl.AppAdaptiveExt_Manual_Adaptive;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 17:42
 */
public class ExtensionLoader_Adaptive_Test {
    @Test
    public void test_use_AdaptiveClass(){
        ExtensionLoader<AppAdaptiveExt> loader = ExtensionLoader.getExtensionLoader(AppAdaptiveExt.class);
        AppAdaptiveExt adaptiveExtension = loader.getAdaptiveExtension();
        assertTrue(adaptiveExtension instanceof AppAdaptiveExt_Manual_Adaptive);
    }

    @Test
    public void test_use_AdaptiveClass2(){
        ExtensionLoader<AppAdaptiveExt> loader = ExtensionLoader.getExtensionLoader(AppAdaptiveExt.class);
        loader.getExtension("impl");
    }
}
