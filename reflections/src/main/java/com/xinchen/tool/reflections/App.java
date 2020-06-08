package com.xinchen.tool.reflections;

import com.xinchen.tool.reflections.model.EntityBase;
import com.xinchen.tool.reflections.model.AnnotationType;
import lombok.extern.slf4j.Slf4j;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Hello world!
 * @author xinchen
 */
@Slf4j
public class App {
    public static void main(String[] args) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("com.xinchen.tool.reflections"))
                // 资源扫描
                .setScanners(new ResourcesScanner())
                // 子类扫描
                .addScanners(new SubTypesScanner())
                // 类注解扫描
                .addScanners(new TypeAnnotationsScanner())
                // 属性注解扫描
                .addScanners(new FieldAnnotationsScanner())
                // 方法注解扫描
                .addScanners(new MethodAnnotationsScanner())
                // 方法参数扫描
                .addScanners(new MethodParameterScanner())
        );


        // 指定接口的子类
        final Set<Class<? extends EntityBase>> subTypes = reflections.getSubTypesOf(EntityBase.class);
        log.info("指定接口 {} 的子类: {}", EntityBase.class,subTypes);

        // 获取资源文件
        final Set<String> resources = reflections.getResources(Pattern.compile(".*\\.properties"));
        log.info("获取资源文件结果： {}",resources);

        // 获取在TYPE上标记的注解的类
        final Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(AnnotationType.class);
        log.info("获取Type注解结果： {}", typesAnnotatedWith);

    }
}
