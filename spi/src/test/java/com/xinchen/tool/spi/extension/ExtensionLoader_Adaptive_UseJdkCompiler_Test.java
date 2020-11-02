package com.xinchen.tool.spi.extension;


import com.xinchen.tool.spi.compiler.support.AdaptiveCompiler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * 切换默认Compiler代码生成
 */
class ExtensionLoader_Adaptive_UseJdkCompiler_Test extends ExtensionLoader_Adaptive_Test {
    @BeforeAll
    static void setUp() throws Exception {
        AdaptiveCompiler.setDefaultCompiler("jdk");
    }

    @AfterAll
    static void tearDown() throws Exception {
        AdaptiveCompiler.setDefaultCompiler("javassist");
    }
}