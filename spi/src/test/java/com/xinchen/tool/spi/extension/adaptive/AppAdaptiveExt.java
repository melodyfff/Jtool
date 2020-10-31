package com.xinchen.tool.spi.extension.adaptive;

import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.Adaptive;
import com.xinchen.tool.spi.extension.SPI;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 16:02
 */
@SPI
public interface AppAdaptiveExt {
    @Adaptive
    String echo(URL url, String message);
}
