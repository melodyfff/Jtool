package com.xinchen.tool.spi.logger.slf4j;

import org.junit.jupiter.api.Test;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 23:29
 */
public class Slf4jLoggerTest {
    @Test
    public void testLocationAwareLogger() {
        LocationAwareLogger locationAwareLogger = mock(LocationAwareLogger.class);
        Slf4jLogger logger = new Slf4jLogger(locationAwareLogger);

        logger.error("error");

        logger.warn("warn");
        logger.info("info");
        logger.debug("debug");
        logger.trace("info");

        verify(locationAwareLogger, times(5)).log(isNull(Marker.class), anyString(),
                anyInt(), anyString(), isNull(Object[].class), isNull(Throwable.class));

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

        verify(locationAwareLogger, times(10)).log(isNull(Marker.class), anyString(),
                anyInt(), anyString(), isNull(Object[].class), any(Throwable.class));
    }
}
