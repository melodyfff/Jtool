package com.xinchen.tool.spi.extension.ext.ext6_wrap.impl;


import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.ext.ext6_wrap.WrappedExt;

public class Ext5Impl1 implements WrappedExt {
    public String echo(URL url, String s) {
        return "Ext5Impl1-echo";
    }
}