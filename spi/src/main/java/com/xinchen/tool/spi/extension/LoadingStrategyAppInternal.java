package com.xinchen.tool.spi.extension;

/**
 * 内置服务加载 最大优先级
 *
 * App internal {@link LoadingStrategy}
 *
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 15:50
 */
public class LoadingStrategyAppInternal implements LoadingStrategy{
    @Override
    public String directory() {
        return "META-INF/app/internal/";
    }

    @Override
    public int getPriority() {
        // 最大优先级
        return MAX_PRIORITY;
    }
}
