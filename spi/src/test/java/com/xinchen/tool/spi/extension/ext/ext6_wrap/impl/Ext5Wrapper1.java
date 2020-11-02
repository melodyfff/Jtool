package com.xinchen.tool.spi.extension.ext.ext6_wrap.impl;


import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.ext.ext6_wrap.WrappedExt;

import java.util.concurrent.atomic.AtomicInteger;

public class Ext5Wrapper1 implements WrappedExt {
    public static AtomicInteger echoCount = new AtomicInteger();
    WrappedExt instance;

    public Ext5Wrapper1(WrappedExt instance) {
        this.instance = instance;
    }

    public String echo(URL url, String s) {
        echoCount.incrementAndGet();
        return instance.echo(url, s);
    }
}