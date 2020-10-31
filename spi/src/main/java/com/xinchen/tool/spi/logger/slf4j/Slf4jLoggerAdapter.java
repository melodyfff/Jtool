package com.xinchen.tool.spi.logger.slf4j;

import com.xinchen.tool.spi.logger.Level;
import com.xinchen.tool.spi.logger.Logger;
import com.xinchen.tool.spi.logger.LoggerAdapter;

import java.io.File;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 22:47
 */
public class Slf4jLoggerAdapter implements LoggerAdapter {

    private Level level;
    private File file;

    @Override
    public Logger getLogger(String key) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
    }

    @Override
    public Logger getLogger(Class<?> key) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }
}
