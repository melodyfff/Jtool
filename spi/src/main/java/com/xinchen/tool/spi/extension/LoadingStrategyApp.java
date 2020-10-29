package com.xinchen.tool.spi.extension;

/**
 * App {@link LoadingStrategy}
 *
 * 应用加载 ExtensionClasses 策略 常规优先级 - 0
 *
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 15:46
 */
public class LoadingStrategyApp implements LoadingStrategy{
    @Override
    public String directory() {
        return "META-INF/app/";
    }

    @Override
    public boolean overridden() {
        return true;
    }

    @Override
    public int getPriority() {
        // 常规优先级 - 0
        return NORMAL_PRIORITY;
    }
}
