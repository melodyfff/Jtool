package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.lang.Prioritized;

/**
 *
 * 加载 ExtensionClasses 策略
 *
 * JDK spi加载
 * resources/META-INF/services/com.xinchen.tool.spi.extension.LoadingStrategy
 *
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 15:34
 */
public interface LoadingStrategy extends Prioritized {
    /** 目录 */
    String directory();

    /**
     * 尝试从{@link ExtensionLoader}的ClassLoader中加载
     * try to load from ExtensionLoader's ClassLoader first
     *
     * @return 默认返回false
     */
    default boolean preferExtensionClassLoader() {
        return false;
    }

    /**
     * 排除扫描的包名
     *
     * @return 默认返回Null
     */
    default String[] excludedPackages() {
        return null;
    }

    /**
     *
     * 表示当前策略是否支持覆盖其他优先级低的实例
     * Indicates current {@link LoadingStrategy} supports overriding other lower prioritized instances or not.
     *
     * @return if supports, return <code>true</code>, or <code>false</code>
     */
    default boolean overridden() {
        return false;
    }
}
