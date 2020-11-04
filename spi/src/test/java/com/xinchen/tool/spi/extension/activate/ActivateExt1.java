package com.xinchen.tool.spi.extension.activate;


import com.xinchen.tool.spi.extension.SPI;

/**
 * 这里的 impl1 并未声明
 */
@SPI("impl1")
public interface ActivateExt1 {
    String echo(String msg);
}
