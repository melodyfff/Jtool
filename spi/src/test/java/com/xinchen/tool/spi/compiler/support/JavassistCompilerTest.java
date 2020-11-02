package com.xinchen.tool.spi.compiler.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * @author xinchen
 * @version 1.0
 * @date 02/11/2020 10:48
 */
public class JavassistCompilerTest extends JavaCodeTest {
    @Test
    public void testCompileJavaClass() throws Exception {
        JavassistCompiler compiler = new JavassistCompiler();
        Class<?> clazz = compiler.compile(getSimpleCode(), JavassistCompiler.class.getClassLoader());

        // Because javassist compiles using the caller class loader, we should't use HelloService directly
        Object instance = clazz.newInstance();
        Method sayHello = instance.getClass().getMethod("sayHello");
        Assertions.assertEquals("Hello world!", sayHello.invoke(instance));
    }

    /**
     * javassist compile will find HelloService in classpath
     */
    @Test
    public void testCompileJavaClass0() throws Exception {
        JavassistCompiler compiler = new JavassistCompiler();
        Class<?> clazz = compiler.compile(getSimpleCodeWithoutPackage(), JavassistCompiler.class.getClassLoader());
        Object instance = clazz.newInstance();
        Method sayHello = instance.getClass().getMethod("sayHello");
        Assertions.assertEquals("Hello world!", sayHello.invoke(instance));
    }

    @Test
    public void testCompileJavaClass1() throws Exception {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            JavassistCompiler compiler = new JavassistCompiler();
            Class<?> clazz = compiler.compile(getSimpleCodeWithSyntax0(), JavassistCompiler.class.getClassLoader());
            Object instance = clazz.newInstance();
            Method sayHello = instance.getClass().getMethod("sayHello");
            Assertions.assertEquals("Hello world!", sayHello.invoke(instance));
        });
    }

    @Test
    public void testCompileJavaClassWithImport() throws Exception {
        JavassistCompiler compiler = new JavassistCompiler();
        Class<?> clazz = compiler.compile(getSimpleCodeWithImports(), JavassistCompiler.class.getClassLoader());
        Object instance = clazz.newInstance();
        Method sayHello = instance.getClass().getMethod("sayHello");
        Assertions.assertEquals("Hello world!", sayHello.invoke(instance));
    }

    @Test
    public void testCompileJavaClassWithExtends() throws Exception {
        JavassistCompiler compiler = new JavassistCompiler();
        Class<?> clazz = compiler.compile(getSimpleCodeWithWithExtends(), JavassistCompiler.class.getClassLoader());
        Object instance = clazz.newInstance();
        Method sayHello = instance.getClass().getMethod("sayHello");
        Assertions.assertEquals("Hello world3!", sayHello.invoke(instance));
    }
}
