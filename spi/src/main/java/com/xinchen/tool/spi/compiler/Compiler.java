package com.xinchen.tool.spi.compiler;

import com.xinchen.tool.spi.extension.SPI;

/**
 *
 * Compiler. (SPI, Singleton, ThreadSafe)
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 14:40
 */
@SPI("javassist")
public interface Compiler {
    /**
     * Compile java source code.
     *
     * @param code        Java source code
     * @param classLoader classloader
     * @return Compiled class
     */
    Class<?> compile(String code, ClassLoader classLoader);
}
