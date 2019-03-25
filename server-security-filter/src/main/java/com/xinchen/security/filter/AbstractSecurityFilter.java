package com.xinchen.security.filter;

/**
 * This is {@link AbstractSecurityFilter}
 *
 * Include loggerHandlerClassName
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/3/25 23:03
 */
public abstract class AbstractSecurityFilter {

    public static final String LOGGER_HANDLER_CLASS_NAME = "loggerHandlerClassName";

    private String loggerHandlerClassName = "org.slf4j.bridge.SLF4JBridgeHandler";

    public String getLoggerHandlerClassName() {
        return loggerHandlerClassName;
    }

    public void setLoggerHandlerClassName(String loggerHandlerClassName) {
        this.loggerHandlerClassName = loggerHandlerClassName;
    }
}
