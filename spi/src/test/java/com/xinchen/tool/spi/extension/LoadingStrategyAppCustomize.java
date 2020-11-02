package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.lang.Prioritized;

/**
 *
 * 自定义加载策略
 *
 * @author xinchen
 * @version 1.0
 * @date 02/11/2020 13:24
 */
public class LoadingStrategyAppCustomize implements LoadingStrategy{

    @Override
    public String directory() {
        return "META-INF/app/customize/";
    }

    @Override
    public boolean overridden() {
        return true;
    }

    @Override
    public int getPriority() {
        // 优先级 < LoadingStrategyAppInternal
        // 优先级 > LoadingStrategyApp
        return Prioritized.MAX_PRIORITY + 1;
    }
}
