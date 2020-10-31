package com.xinchen.tool.spi;

/**
 *
 * Version
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/30 22:46
 */
public final class Version {
    // TODO 完善
    /** 实际版本通常为jar版本 */
    private static final String VERSION = getVersion(Version.class, "UNKOWN VERSION");

    public static String getVersion() {
        return VERSION;
    }

    public static String getVersion(Class<?> cls, String defaultVersion) {
        return "1.0";
    }
}
