package com.xinchen.tool.spi.logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Logger Factory
 *
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 10:48
 */
public class LoggerFactory {
    private static final Map LOGGERS = new ConcurrentHashMap();
}
