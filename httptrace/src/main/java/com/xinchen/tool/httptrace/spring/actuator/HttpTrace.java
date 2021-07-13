package com.xinchen.tool.httptrace.spring.actuator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * mark Method to be trace.
 *
 *
 * @author xinchen
 * @version 1.0
 * @date 08/06/2020 14:20
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpTrace {
    String model() default "http-trace";
    String value() default "";
}
