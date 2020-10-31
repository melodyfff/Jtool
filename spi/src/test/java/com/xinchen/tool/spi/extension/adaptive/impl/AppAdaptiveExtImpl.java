package com.xinchen.tool.spi.extension.adaptive.impl;

import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.adaptive.AppAdaptiveExt;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 16:04
 */
public class AppAdaptiveExtImpl implements AppAdaptiveExt {
    @Override
    public String echo(URL url, String message) {
        return this.getClass().getSimpleName();
    }
}
