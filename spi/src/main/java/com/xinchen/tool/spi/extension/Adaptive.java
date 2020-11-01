package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.URL;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Provide helpful information for {@link ExtensionLoader} to inject dependency extension instance.
 *
 * 1. 在类上加上@Adaptive注解的类，是最为明确的创建对应类型Adaptive类。所以他优先级最高
 * 2. @SPI注解中的value是默认值，如果通过URL获取不到关于取哪个类作为Adaptive类的话，就使用这个默认值
 * 3. 可以在接口方法上增加@Adaptive注解，注解中的value与URL中的参数的key一致，URL中的key对应的value就是spi中的name,获取相应的实现类
 * 4. @Adaptive注解的类，即使在/META-INF/app/internal 中配置了别名如: adaptive=...  通过URL(key=adaptive)去寻找其实现类会报错No such extension 。因为在{@link ExtensionLoader cachedClasses}中不会保存Adaptive的类信息
 *
 *
 * @see ExtensionLoader
 *
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 11:50
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Adaptive {
    /**
     *
     * 决定哪一个拓展被注入。
     *
     *
     * Decide which target extension to be injected. The name of the target extension is decided by the parameter passed
     * in the URL, and the parameter names are given by this method.
     * <p>
     * If the specified parameters are not found from {@link URL}, then the default extension will be used for
     * dependency injection (specified in its interface's {@link SPI}).
     * <p>
     * For example, given <code>String[] {"key1", "key2"}</code>:
     * <ol>
     * <li>find parameter 'key1' in URL, use its value as the extension's name</li>
     * <li>try 'key2' for extension's name if 'key1' is not found (or its value is empty) in URL</li>
     * <li>use default extension if 'key2' doesn't exist either</li>
     * <li>otherwise, throw {@link IllegalStateException}</li>
     * </ol>
     * If the parameter names are empty, then a default parameter name is generated from interface's
     * class name with the rule: divide classname from capital char into several parts, and separate the parts with
     * dot '.', for example, for {@code org.apache.dubbo.xxx.YyyInvokerWrapper}, the generated name is
     * <code>String[] {"yyy.invoker.wrapper"}</code>.
     *
     * @return parameter names in URL
     */
    String[] value() default {};
}
