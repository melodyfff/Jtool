package com.xinchen.tool.spi.extension.factory;

import com.xinchen.tool.spi.extension.ExtensionFactory;
import com.xinchen.tool.spi.extension.ExtensionLoader;
import com.xinchen.tool.spi.extension.SPI;

/**
 *
 * SpiExtensionFactory
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 18:02
 */
public class SpiExtensionFactory implements ExtensionFactory {
    @Override
    public <T> T getExtension(Class<T> type, String name) {
        // 检查类型和@SPI
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)){
            final ExtensionLoader<T> loader = ExtensionLoader.getExtensionLoader(type);
            if (!loader.getSupportedExtensions().isEmpty()){
                return loader.getAdaptiveExtension();
            }
        }
        return null;
    }
}
