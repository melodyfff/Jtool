package com.xinchen.tool.spi.extension.ext.ext7.impl;
import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.ext.ext7.InitErrorExt;

public class Ext7InitErrorImpl implements InitErrorExt {

    static {
        if (true) {
            throw new RuntimeException("intended!");
        }
    }

    public String echo(URL url, String s) {
        return "";
    }

}