package com.xinchen.security.filter;

import java.util.logging.Formatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/3/25 23:51
 */
public final class FilterUtils {
    private static final Logger LOGGER = Logger.getLogger(FilterUtils.class.getName());

    /**
     * default : false
     * throw the errors (RuntimeException)
     */
    public static boolean throwOnErrors;

    private FilterUtils() {
    }

    public static void setThrowOnErrors(final boolean throwOnErrors) {
        FilterUtils.throwOnErrors = throwOnErrors;
    }

    /**
     * 转换String字符串为boolean
     * @param string2Parse true/false
     * @return boolean
     */
    public static boolean parseStringToBooleanDefaultingToFalse(final String string2Parse) {
        return Boolean.parseBoolean(string2Parse);
    }


    /**
     * 配置日志处理
     * @param loggerHandlerClassName loggerHandlerClassName
     * @param logger logger
     */
    public static void configureLogging(final String loggerHandlerClassName, final Logger logger) {
        final Handler handler = loadLoggerHandlerByClassName(loggerHandlerClassName);
        configureLogging(handler, logger);
    }

    /**
     * 配置日志处理
     * @param handler handler
     * @param logger logger
     */
    public static void configureLogging(final Handler handler, final Logger logger) {
        for (final Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }
        // no root
        logger.setUseParentHandlers(false);

        // default logging to console
        if (handler == null) {
            final ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new Formatter() {
                @Override
                public String format(final LogRecord record) {
                    final StringBuffer sb = new StringBuffer();

                    sb.append("[");
                    sb.append(record.getLevel().getName());
                    sb.append("]\t");

                    sb.append(formatMessage(record));
                    sb.append("\n");

                    return sb.toString();
                }
            });
            logger.addHandler(consoleHandler);
        } else {
            logger.addHandler(handler);
        }

    }

    /**
     * 打印异常信息，通过 throwOnErrors 判断是否继续抛出异常
     * @param logger logger
     * @param ex Exception
     */
    public static void logException(final Logger logger, final Exception ex) {
        logger.log(Level.SEVERE, ex.getMessage(), ex);
        if (throwOnErrors) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 通过加载日志处理器
     * @param loggerHandlerClassName  loggerHandlerClassName
     * @return {@link java.util.logging.Handler}
     */
    private static Handler loadLoggerHandlerByClassName(final String loggerHandlerClassName) {
        try {
            if (loggerHandlerClassName == null) {
                return null;
            }

            final ClassLoader classLoader = RequestParameterPolicyEnforcementFilter.class.getClassLoader();
            final Class loggerHandlerClass = classLoader.loadClass(loggerHandlerClassName);
            if (loggerHandlerClass != null) {
                return (Handler) loggerHandlerClass.newInstance();
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, e.getMessage(), e);
        }
        return null;
    }

}