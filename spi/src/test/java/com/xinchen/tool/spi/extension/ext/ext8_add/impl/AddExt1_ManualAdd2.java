package com.xinchen.tool.spi.extension.ext.ext8_add.impl;

import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.ext.ext8_add.AddExt1;
public class AddExt1_ManualAdd2 implements AddExt1 {
    public String echo(URL url, String s) {
        return this.getClass().getSimpleName();
    }
}