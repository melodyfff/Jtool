package com.xinchen.tool.spi.extension.ext.ext7;


import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.Adaptive;
import com.xinchen.tool.spi.extension.SPI;

/**
 * Test scenario for DUBBO-144: extension load failure when third-party dependency doesn't exist. If extension is not
 * referenced/used, do not report error in load time (instead, report issue when it gets used)
 */
@SPI
public interface InitErrorExt {
    @Adaptive
    String echo(URL url, String s);
}