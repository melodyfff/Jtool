package com.xinchen.tool.spi.logger;

import com.xinchen.tool.spi.logger.jcl.JclLogger;
import com.xinchen.tool.spi.logger.jcl.JclLoggerAdapter;
import com.xinchen.tool.spi.logger.jdk.JdkLogger;
import com.xinchen.tool.spi.logger.jdk.JdkLoggerAdapter;
import com.xinchen.tool.spi.logger.log4j.Log4jLogger;
import com.xinchen.tool.spi.logger.log4j.Log4jLoggerAdapter;
import com.xinchen.tool.spi.logger.log4j2.Log4j2Logger;
import com.xinchen.tool.spi.logger.log4j2.Log4j2LoggerAdapter;
import com.xinchen.tool.spi.logger.slf4j.Slf4jLogger;
import com.xinchen.tool.spi.logger.slf4j.Slf4jLoggerAdapter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 23:17
 */
public class LoggerAdapterTest {
    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of(JclLoggerAdapter.class, JclLogger.class),
                Arguments.of(JdkLoggerAdapter.class, JdkLogger.class),
                Arguments.of(Log4jLoggerAdapter.class, Log4jLogger.class),
                Arguments.of(Slf4jLoggerAdapter.class, Slf4jLogger.class),
                Arguments.of(Log4j2LoggerAdapter.class, Log4j2Logger.class)
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testGetLogger(Class<? extends LoggerAdapter> loggerAdapterClass, Class<? extends Logger> loggerClass) throws IllegalAccessException, InstantiationException {
        LoggerAdapter loggerAdapter = loggerAdapterClass.newInstance();
        Logger logger = loggerAdapter.getLogger(this.getClass());
        assertThat(logger.getClass().isAssignableFrom(loggerClass), is(true));

        logger = loggerAdapter.getLogger(this.getClass().getSimpleName());
        assertThat(logger.getClass().isAssignableFrom(loggerClass), is(true));

    }

    @ParameterizedTest
    @MethodSource("data")
    public void testLevel(Class<? extends LoggerAdapter> loggerAdapterClass) throws IllegalAccessException, InstantiationException {
        LoggerAdapter loggerAdapter = loggerAdapterClass.newInstance();
        for (Level targetLevel : Level.values()) {
            loggerAdapter.setLevel(targetLevel);
            assertThat(loggerAdapter.getLevel(), is(targetLevel));
        }
    }
}
