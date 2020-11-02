package com.xinchen.tool.spi.extension;


import com.xinchen.tool.spi.extension.compatible.CompatibleExt;
import com.xinchen.tool.spi.extension.compatible.impl.CompatibleExtImpl1;
import com.xinchen.tool.spi.extension.compatible.impl.CompatibleExtImpl2;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class ExtensionLoader_Compatible_Test {

    @Test
    public void test_getExtension() throws Exception {
        // com.xinchen.tool.spi.extension.compatible.impl.CompatibleExtImpl1
        // impl2=com.xinchen.tool.spi.extension.compatible.impl.CompatibleExtImpl2

        // 没有key的时候默认名字为全小写，或者去Service后缀
        assertTrue(ExtensionLoader.getExtensionLoader(CompatibleExt.class).getExtension("compatibleextimpl1") instanceof CompatibleExtImpl1);
        assertTrue(ExtensionLoader.getExtensionLoader(CompatibleExt.class).getExtension("impl2") instanceof CompatibleExtImpl2);
    }
}