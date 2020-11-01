package com.xinchen.tool.spi.extension.ext.ext4;


import com.xinchen.tool.spi.extension.Adaptive;
import com.xinchen.tool.spi.extension.SPI;

import java.util.List;

@SPI("impl1")
public interface NoUrlParamExt {
    // method has no URL parameter
    @Adaptive
    String bark(String name, List<Object> list);
}