
package com.xinchen.tool.spi.extension.activate.impl;


import com.xinchen.tool.spi.extension.Activate;
import com.xinchen.tool.spi.extension.activate.ActivateExt1;

@Activate(value = {"value"}, group = {"value"})
public class ValueActivateExtImpl implements ActivateExt1 {

    public String echo(String msg) {
        return msg;
    }
}
