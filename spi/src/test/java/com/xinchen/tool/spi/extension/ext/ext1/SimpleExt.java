package com.xinchen.tool.spi.extension.ext.ext1;

import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.Adaptive;
import com.xinchen.tool.spi.extension.SPI;

/**
 *
 * Simple extension, has no wrapper
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/11/1 12:19
 */
@SPI("impl1")
public interface SimpleExt {
    // @Adaptive example, do not specify a explicit key.
    @Adaptive
    String echo(URL url, String s);

    @Adaptive({"key1", "key2"})
    String yell(URL url, String s);

    // no @Adaptive
    String bang(URL url, int i);
}
