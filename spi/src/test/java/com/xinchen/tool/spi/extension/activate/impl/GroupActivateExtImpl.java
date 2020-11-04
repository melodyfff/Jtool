
package com.xinchen.tool.spi.extension.activate.impl;


import com.xinchen.tool.spi.extension.Activate;
import com.xinchen.tool.spi.extension.activate.ActivateExt1;

@Activate(group = {"group1", "group2"})
public class GroupActivateExtImpl implements ActivateExt1 {

    public String echo(String msg) {
        return msg;
    }
}
