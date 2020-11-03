package com.xinchen.tool.spi.extension.ext.ext8_add.impl;

import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.ext.ext8_add.AddExt2;
public class AddExt2Impl1 implements AddExt2 {
    public String echo(URL url, String s) {
        return this.getClass().getSimpleName();
    }
}