package com.xinchen.tool.spi.extension;

/**
 *
 * The annotated class will only work as a wrapper when the condition matches.
 *
 * @author xinchen
 * @version 1.0
 * @date 30/10/2020 15:45
 */
public @interface Wrapper {
    /**
     * the extension names that need to be wrapped.
     */
    String[] matches() default {};

    /**
     * the extension names that need to be excluded.
     */
    String[] mismatches() default {};
}
