package com.xinchen.tool.spi.extension.activate.impl;


import com.xinchen.tool.spi.extension.Activate;
import com.xinchen.tool.spi.extension.activate.ActivateExt1;

@Activate(group = "old_group")
public class OldActivateExt1Impl2 implements ActivateExt1 {
    public String echo(String msg) {
        return msg;
    }
}
