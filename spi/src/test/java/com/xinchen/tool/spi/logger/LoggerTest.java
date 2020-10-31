package com.xinchen.tool.spi.logger;

import com.xinchen.tool.spi.logger.jcl.JclLoggerAdapter;
import com.xinchen.tool.spi.logger.jdk.JdkLoggerAdapter;
import com.xinchen.tool.spi.logger.log4j.Log4jLoggerAdapter;
import com.xinchen.tool.spi.logger.log4j2.Log4j2LoggerAdapter;
import com.xinchen.tool.spi.logger.slf4j.Slf4jLoggerAdapter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 23:35
 */
public class LoggerTest {
    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of(JclLoggerAdapter.class),
                Arguments.of(JdkLoggerAdapter.class),
                Arguments.of(Log4jLoggerAdapter.class),
                Arguments.of(Slf4jLoggerAdapter.class),
                Arguments.of(Log4j2LoggerAdapter.class)
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testAllLogMethod(Class<? extends LoggerAdapter> loggerAdapter) throws Exception {
        LoggerAdapter adapter = loggerAdapter.newInstance();
        adapter.setLevel(Level.ALL);
        Logger logger = adapter.getLogger(this.getClass());
        logger.error("error");
        logger.warn("warn");
        logger.info("info");
        logger.debug("debug");
        logger.trace("info");

        logger.error(new Exception("error"));
        logger.warn(new Exception("warn"));
        logger.info(new Exception("info"));
        logger.debug(new Exception("debug"));
        logger.trace(new Exception("trace"));

        logger.error("error", new Exception("error"));
        logger.warn("warn", new Exception("warn"));
        logger.info("info", new Exception("info"));
        logger.debug("debug", new Exception("debug"));
        logger.trace("trace", new Exception("trace"));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testLevelEnable(Class<? extends LoggerAdapter> loggerAdapter) throws IllegalAccessException, InstantiationException {
        LoggerAdapter adapter = loggerAdapter.newInstance();
        adapter.setLevel(Level.ALL);
        Logger logger = adapter.getLogger(this.getClass());
        assertThat(logger.isWarnEnabled(), not(nullValue()));
        assertThat(logger.isTraceEnabled(), not(nullValue()));
        assertThat(logger.isErrorEnabled(), not(nullValue()));
        assertThat(logger.isInfoEnabled(), not(nullValue()));
        assertThat(logger.isDebugEnabled(), not(nullValue()));
    }
}
