package com.xinchen.tool.spi.logger.support;

import com.xinchen.tool.spi.logger.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 23:34
 */
public class FailsafeLoggerTest {
    @Test
    public void testFailSafeForLoggingMethod() {
        Logger failLogger = mock(Logger.class);
        FailsafeLogger failsafeLogger = new FailsafeLogger(failLogger);

        doThrow(new RuntimeException()).when(failLogger).error(anyString());
        doThrow(new RuntimeException()).when(failLogger).warn(anyString());
        doThrow(new RuntimeException()).when(failLogger).info(anyString());
        doThrow(new RuntimeException()).when(failLogger).debug(anyString());
        doThrow(new RuntimeException()).when(failLogger).trace(anyString());

        failsafeLogger.error("error");
        failsafeLogger.warn("warn");
        failsafeLogger.info("info");
        failsafeLogger.debug("debug");
        failsafeLogger.trace("info");

        doThrow(new RuntimeException()).when(failLogger).error(any(Throwable.class));
        doThrow(new RuntimeException()).when(failLogger).warn(any(Throwable.class));
        doThrow(new RuntimeException()).when(failLogger).info(any(Throwable.class));
        doThrow(new RuntimeException()).when(failLogger).debug(any(Throwable.class));
        doThrow(new RuntimeException()).when(failLogger).trace(any(Throwable.class));

        failsafeLogger.error(new Exception("error"));
        failsafeLogger.warn(new Exception("warn"));
        failsafeLogger.info(new Exception("info"));
        failsafeLogger.debug(new Exception("debug"));
        failsafeLogger.trace(new Exception("trace"));

        failsafeLogger.error("error", new Exception("error"));
        failsafeLogger.warn("warn", new Exception("warn"));
        failsafeLogger.info("info", new Exception("info"));
        failsafeLogger.debug("debug", new Exception("debug"));
        failsafeLogger.trace("trace", new Exception("trace"));
    }

    @Test
    public void testSuccessLogger() {
        Logger successLogger = mock(Logger.class);
        FailsafeLogger failsafeLogger = new FailsafeLogger(successLogger);
        failsafeLogger.error("error");
        failsafeLogger.warn("warn");
        failsafeLogger.info("info");
        failsafeLogger.debug("debug");
        failsafeLogger.trace("info");

        verify(successLogger).error(anyString());
        verify(successLogger).warn(anyString());
        verify(successLogger).info(anyString());
        verify(successLogger).debug(anyString());
        verify(successLogger).trace(anyString());

        failsafeLogger.error(new Exception("error"));
        failsafeLogger.warn(new Exception("warn"));
        failsafeLogger.info(new Exception("info"));
        failsafeLogger.debug(new Exception("debug"));
        failsafeLogger.trace(new Exception("trace"));

        failsafeLogger.error("error", new Exception("error"));
        failsafeLogger.warn("warn", new Exception("warn"));
        failsafeLogger.info("info", new Exception("info"));
        failsafeLogger.debug("debug", new Exception("debug"));
        failsafeLogger.trace("trace", new Exception("trace"));
    }

    @Test
    public void testGetLogger() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Logger failLogger = mock(Logger.class);
            FailsafeLogger failsafeLogger = new FailsafeLogger(failLogger);

            doThrow(new RuntimeException()).when(failLogger).error(anyString());
            failsafeLogger.getLogger().error("should get error");
        });
    }
}
