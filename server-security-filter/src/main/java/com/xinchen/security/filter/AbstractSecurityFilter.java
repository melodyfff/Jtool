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


    /**
     * 描述日志处理类名，如果没有定义过滤器会切换到{@link java.util.logging.ConsoleHandler}
     */
    public static final String LOGGER_HANDLER_CLASS_NAME = "loggerHandlerClassName";


    /**
     * {@link org.slf4j.bridge.SLF4JBridgeHandler}
     * Bridge/route all JUL log records to the SLF4J API
     */
    private String loggerHandlerClassName = "org.slf4j.bridge.SLF4JBridgeHandler";

    public String getLoggerHandlerClassName() {
        return loggerHandlerClassName;
    }

    public void setLoggerHandlerClassName(String loggerHandlerClassName) {
        this.loggerHandlerClassName = loggerHandlerClassName;
    }
}
