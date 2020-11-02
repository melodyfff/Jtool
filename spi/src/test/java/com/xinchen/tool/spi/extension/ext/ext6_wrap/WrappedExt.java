package com.xinchen.tool.spi.extension.ext.ext6_wrap;

import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.SPI;

/**
 * No Adaptive Method!!
 */
@SPI("impl1")
public interface WrappedExt {

    String echo(URL url, String s);
}