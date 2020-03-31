package com.xinchen.tool.reflections;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

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


        final Set<String> resources = reflections.getResources(Pattern.compile(".*\\.properties"));
        log.info("获取资源文件结果： {}",resources);
    }
}
