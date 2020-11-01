package com.xinchen.tool.spi.extension.ext.ext1.impl;


import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.ext.ext1.SimpleExt;

public class SimpleExtImpl2 implements SimpleExt {
    public String echo(URL url, String s) {
        return "Ext1Impl2-echo";
    }

    public String yell(URL url, String s) {
        return "Ext1Impl2-yell";
    }

    public String bang(URL url, int i) {
        return "bang2";
    }

}