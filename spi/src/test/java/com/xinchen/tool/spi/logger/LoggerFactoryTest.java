package com.xinchen.tool.spi.logger;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 23:03
 */
public class LoggerFactoryTest {
    @Test
    public void testLoggerLevel() {
        LoggerFactory.setLevel(Level.INFO);
        Level level = LoggerFactory.getLevel();

        assertThat(level, is(Level.INFO));
    }

    @Test
    public void testGetLogFile() {
        LoggerFactory.setLoggerAdapter("slf4j");
        File file = LoggerFactory.getFile();

        assertThat(file, is(nullValue()));
    }

    @Test
    public void testAllLogLevel() {
        for (Level targetLevel : Level.values()) {
            LoggerFactory.setLevel(targetLevel);
            Level level = LoggerFactory.getLevel();

            assertThat(level, is(targetLevel));
        }
    }

    @Test
    public void testGetLogger() {
        Logger logger1 = LoggerFactory.getLogger(this.getClass());
        Logger logger2 = LoggerFactory.getLogger(this.getClass());

        assertThat(logger1, is(logger2));
    }

    @Test
    public void shouldReturnSameLogger() {
        Logger logger1 = LoggerFactory.getLogger(this.getClass().getName());
        Logger logger2 = LoggerFactory.getLogger(this.getClass().getName());

        assertThat(logger1, is(logger2));
    }
}
