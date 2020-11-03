package com.xinchen.tool.spi.extension.ext.ext8_add.impl;

import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.Adaptive;
import com.xinchen.tool.spi.extension.ExtensionLoader;
import com.xinchen.tool.spi.extension.ext.ext8_add.AddExt2;
@Adaptive
public class AddExt2_ManualAdaptive implements AddExt2 {
    public String echo(URL url, String s) {
        AddExt2 addExt1 = ExtensionLoader.getExtensionLoader(AddExt2.class).getExtension(url.getParameter("add.ext2"));
        return addExt1.echo(url, s);
    }
}