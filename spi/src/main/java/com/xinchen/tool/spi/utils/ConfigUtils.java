package com.xinchen.tool.spi.utils;

/**
 * @author xinchen
 * @version 1.0
 * @date 04/11/2020 11:40
 */
public final class ConfigUtils {

    private ConfigUtils() {
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static boolean isEmpty(String value) {
        return StringUtils.isEmpty(value)
                || "false".equalsIgnoreCase(value)
                || "0".equalsIgnoreCase(value)
                || "null".equalsIgnoreCase(value)
                || "N/A".equalsIgnoreCase(value);
    }
}
