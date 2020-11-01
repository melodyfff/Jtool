package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.URL;

/**
 *
 * Has no SPI annotation
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/11/1 0:49
 */
public interface NoSpiExt {
    @Adaptive
    String echo(URL url, String s);
}
