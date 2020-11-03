package com.xinchen.tool.spi.extension.ext.ext8_add;


import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.Adaptive;
import com.xinchen.tool.spi.extension.SPI;

/**
 * show add extension pragmatically
 */
@SPI("impl1")
public interface AddExt1 {
    @Adaptive
    String echo(URL url, String s);
}