package com.xinchen.tool.spi.extension;

/**
 * ExtensionFactory
 *
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 10:55
 */
@SPI
public interface ExtensionFactory {
    /**
     * Get extension.
     *
     * @param type object type.
     * @param name object name.
     * @return object instance.
     */
    <T> T getExtension(Class<T> type, String name);
}
