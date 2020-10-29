package com.xinchen.tool.spi.extension;

/**
 * Services {@link LoadingStrategy}
 * JDK default path is "META-INF/services/"
 * 最小优先级
 *
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 15:40
 */
public class LoadingStrategyServices implements LoadingStrategy{
    @Override
    public String directory() {
        return "META-INF/services/";
    }

    @Override
    public boolean overridden() {
        // 允许覆盖其他实例
        return true;
    }

    @Override
    public int getPriority() {
        // 最小优先级
        return MIN_PRIORITY;
    }
}
