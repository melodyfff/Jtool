package com.xinchen.tool.httptrace.core.loader;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @date 2021-07-13 14:46
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface LoadLevel {
  /**
   * Name string.
   *
   * @return the string
   */
  String name();

  /**
   * Order int.
   *
   * @return the int
   */
  int order() default 0;

  /**
   * Scope enum.
   * @return
   */
  Scope scope() default Scope.SINGLETON;
}
