package com.xinchen.tool.spi.logger;

import com.xinchen.tool.spi.extension.SPI;

import java.io.File;

/**
 *
 * Logger provider
 *
 * resources/META-INF/app/internal/com.xinchen.tool.spi.logger.LoggerAdapter 配置加载
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/30 22:34
 */
@SPI
public interface LoggerAdapter {
    /**
     * Get a logger
     *
     * @param key the returned logger will be named after clazz
     * @return logger
     */
    Logger getLogger(Class<?> key);

    /**
     * Get a logger
     *
     * @param key the returned logger will be named after key
     * @return logger
     */
    Logger getLogger(String key);

    /**
     * Get the current logging level
     *
     * @return current logging level
     */
    Level getLevel();

    /**
     * Set the current logging level
     *
     * @param level logging level
     */
    void setLevel(Level level);

    /**
     * Get the current logging file
     *
     * @return current logging file
     */
    File getFile();

    /**
     * Set the current logging file
     *
     * @param file logging file
     */
    void setFile(File file);
}
