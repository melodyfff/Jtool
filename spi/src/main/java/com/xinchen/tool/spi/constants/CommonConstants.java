package com.xinchen.tool.spi.constants;


import java.util.regex.Pattern;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/30 23:38
 */
public interface CommonConstants {

    /** key1,key2... */
    Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");


    String LOCALHOST_VALUE = "127.0.0.1";

    String DEFAULT_KEY_PREFIX = "default.";
    String INTERFACE_KEY = "interface";
    String GROUP_KEY = "group";
    String VERSION_KEY = "version";
    String TIMESTAMP_KEY = "timestamp";

    String METHODS_KEY = "methods";
    String METHOD_KEY = "method";

    String DEFAULT_KEY = "default";

    String REMOVE_VALUE_PREFIX = "-";
}
