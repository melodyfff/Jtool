package com.xinchen.tool.spi.extension;


import com.xinchen.tool.spi.lang.Prioritized;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 16:59
 */
class LoadingStrategyTest {

    @Test
    void test_LoadingStrategy(){
        final List<LoadingStrategy> loadingStrategies = ExtensionLoader.getLoadingStrategies();
        // 按照优先级输出
        loadingStrategies.forEach(x-> System.out.println(x.string()));
    }

    @Test
    void test_GetLoadingStrategies() {
        List<LoadingStrategy> strategies = ExtensionLoader.getLoadingStrategies();

        assertEquals(4, strategies.size());

        int i = 0;

        LoadingStrategy loadingStrategy = strategies.get(i++);
        assertEquals(LoadingStrategyAppInternal.class, loadingStrategy.getClass());
        assertEquals(Prioritized.MAX_PRIORITY, loadingStrategy.getPriority());

        loadingStrategy = strategies.get(i++);
        assertEquals(LoadingStrategyAppCustomize.class, loadingStrategy.getClass());
        assertEquals(Prioritized.MAX_PRIORITY + 1, loadingStrategy.getPriority());


        loadingStrategy = strategies.get(i++);
        assertEquals(LoadingStrategyApp.class, loadingStrategy.getClass());
        assertEquals(Prioritized.NORMAL_PRIORITY, loadingStrategy.getPriority());

        loadingStrategy = strategies.get(i++);
        assertEquals(LoadingStrategyServices.class, loadingStrategy.getClass());
        assertEquals(Prioritized.MIN_PRIORITY, loadingStrategy.getPriority());
    }


}