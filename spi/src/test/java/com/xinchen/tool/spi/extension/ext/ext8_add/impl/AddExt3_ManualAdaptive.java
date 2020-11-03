package com.xinchen.tool.spi.extension.ext.ext8_add.impl;

import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.Adaptive;
import com.xinchen.tool.spi.extension.ExtensionLoader;
import com.xinchen.tool.spi.extension.ext.ext8_add.AddExt3;
@Adaptive
public class AddExt3_ManualAdaptive implements AddExt3 {
    public String echo(URL url, String s) {
        AddExt3 addExt1 = ExtensionLoader.getExtensionLoader(AddExt3.class).getExtension(url.getParameter("add.ext3"));
        return addExt1.echo(url, s);
    }
}