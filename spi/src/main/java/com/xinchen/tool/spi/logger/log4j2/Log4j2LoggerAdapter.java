package com.xinchen.tool.spi.logger.log4j2;

import com.xinchen.tool.spi.logger.Level;
import com.xinchen.tool.spi.logger.Logger;
import com.xinchen.tool.spi.logger.LoggerAdapter;
import org.apache.logging.log4j.LogManager;

import java.io.File;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 22:50
 */
public class Log4j2LoggerAdapter implements LoggerAdapter {

    private Level level;

    public Log4j2LoggerAdapter() {
    }

    private static org.apache.logging.log4j.Level toLog4j2Level(Level level) {
        if (level == Level.ALL) {
            return org.apache.logging.log4j.Level.ALL;
        }
        if (level == Level.TRACE) {
            return org.apache.logging.log4j.Level.TRACE;
        }
        if (level == Level.DEBUG) {
            return org.apache.logging.log4j.Level.DEBUG;
        }
        if (level == Level.INFO) {
            return org.apache.logging.log4j.Level.INFO;
        }
        if (level == Level.WARN) {
            return org.apache.logging.log4j.Level.WARN;
        }
        if (level == Level.ERROR) {
            return org.apache.logging.log4j.Level.ERROR;
        }
        return org.apache.logging.log4j.Level.OFF;
    }

    private static Level fromLog4j2Level(org.apache.logging.log4j.Level level) {
        if (level == org.apache.logging.log4j.Level.ALL) {
            return Level.ALL;
        }
        if (level == org.apache.logging.log4j.Level.TRACE) {
            return Level.TRACE;
        }
        if (level == org.apache.logging.log4j.Level.DEBUG) {
            return Level.DEBUG;
        }
        if (level == org.apache.logging.log4j.Level.INFO) {
            return Level.INFO;
        }
        if (level == org.apache.logging.log4j.Level.WARN) {
            return Level.WARN;
        }
        if (level == org.apache.logging.log4j.Level.ERROR) {
            return Level.ERROR;
        }
        return Level.OFF;
    }

    @Override
    public Logger getLogger(Class<?> key) {
        return new Log4j2Logger(LogManager.getLogger(key));
    }

    @Override
    public Logger getLogger(String key) {
        return new Log4j2Logger(LogManager.getLogger(key));
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
        return null;
    }

    @Override
    public void setFile(File file) {
    }
}
