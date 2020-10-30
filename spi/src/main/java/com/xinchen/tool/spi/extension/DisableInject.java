package com.xinchen.tool.spi.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 在创建实例时标识不进行IOC注入
 *
 *
 * @see Injects
 * @see ExtensionLoader
 *
 * @author xinchen
 * @version 1.0
 * @date 30/10/2020 14:08
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DisableInject {
}
