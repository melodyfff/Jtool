package com.xinchen.tool.spi.compiler.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author xinchen
 * @version 1.0
 * @date 02/11/2020 10:23
 */
class ClassHelperTest {

    @Test
    void testNewInstance() {
        HelloServiceImpl0 instance = (HelloServiceImpl0) ClassHelper.newInstance(HelloServiceImpl0.class.getName());
        Assertions.assertEquals("Hello world!", instance.sayHello());
    }

    @Test
    void testNewInstance0() {
        Assertions.assertThrows(IllegalStateException.class, () -> ClassHelper.newInstance(PrivateHelloServiceImpl.class.getName()));
    }


    @Test
    void testNewInstance1() {
        // final 类型的不能反射初始化
        Assertions.assertThrows(IllegalStateException.class, () -> ClassHelper.newInstance("com.xinchen.tool.spi.compiler.support.internal.HelloServiceInternalImpl"));
    }

    @Test
    void testNewInstance2() {
        // 不存在的类
        Assertions.assertThrows(IllegalStateException.class, () -> ClassHelper.newInstance("com.xinchen.tool.spi.compiler.support.internal.NotExistsImpl"));
    }

    @Test
    void testForName() {
        ClassHelper.forName(new String[]{"com.xinchen.tool.spi.compiler.support"}, "HelloServiceImpl0");
    }

    @Test
    void testForName1() {
        Assertions.assertThrows(IllegalStateException.class, () -> ClassHelper.forName(new String[]{"com.xinchen.tool.spi.compiler.support"}, "HelloServiceImplXX"));
    }


    @Test
    void testForName2() {
        ClassHelper.forName("boolean");
        ClassHelper.forName("byte");
        ClassHelper.forName("char");
        ClassHelper.forName("short");
        ClassHelper.forName("int");
        ClassHelper.forName("long");
        ClassHelper.forName("float");
        ClassHelper.forName("double");
        ClassHelper.forName("boolean[]");
        ClassHelper.forName("byte[]");
        ClassHelper.forName("char[]");
        ClassHelper.forName("short[]");
        ClassHelper.forName("int[]");
        ClassHelper.forName("long[]");
        ClassHelper.forName("float[]");
        ClassHelper.forName("double[]");
    }

    @Test
    void testGetBoxedClass() {
        Assertions.assertEquals(Boolean.class, ClassHelper.getBoxedClass(boolean.class));
        Assertions.assertEquals(Character.class, ClassHelper.getBoxedClass(char.class));
        Assertions.assertEquals(Byte.class, ClassHelper.getBoxedClass(byte.class));
        Assertions.assertEquals(Short.class, ClassHelper.getBoxedClass(short.class));
        Assertions.assertEquals(Integer.class, ClassHelper.getBoxedClass(int.class));
        Assertions.assertEquals(Long.class, ClassHelper.getBoxedClass(long.class));
        Assertions.assertEquals(Float.class, ClassHelper.getBoxedClass(float.class));
        Assertions.assertEquals(Double.class, ClassHelper.getBoxedClass(double.class));
        Assertions.assertEquals(ClassHelperTest.class, ClassHelper.getBoxedClass(ClassHelperTest.class));
    }

    @Test
    void testBoxedAndUnboxed() {
        Assertions.assertEquals(Boolean.valueOf(true), ClassHelper.boxed(true));
        Assertions.assertEquals(Character.valueOf('0'), ClassHelper.boxed('0'));
        Assertions.assertEquals(Byte.valueOf((byte) 0), ClassHelper.boxed((byte) 0));
        Assertions.assertEquals(Short.valueOf((short) 0), ClassHelper.boxed((short) 0));
        Assertions.assertEquals(Integer.valueOf((int) 0), ClassHelper.boxed((int) 0));
        Assertions.assertEquals(Long.valueOf((long) 0), ClassHelper.boxed((long) 0));
        Assertions.assertEquals(Float.valueOf((float) 0), ClassHelper.boxed((float) 0));
        Assertions.assertEquals(Double.valueOf((double) 0), ClassHelper.boxed((double) 0));

        Assertions.assertTrue(ClassHelper.unboxed(Boolean.valueOf(true)));
        Assertions.assertEquals('0', ClassHelper.unboxed(Character.valueOf('0')));
        Assertions.assertEquals((byte) 0, ClassHelper.unboxed(Byte.valueOf((byte) 0)));
        Assertions.assertEquals((short) 0, ClassHelper.unboxed(Short.valueOf((short) 0)));
        Assertions.assertEquals(0, ClassHelper.unboxed(Integer.valueOf((int) 0)));
        Assertions.assertEquals((long) 0, ClassHelper.unboxed(Long.valueOf((long) 0)));
        Assertions.assertEquals((float) 0, ClassHelper.unboxed(Float.valueOf((float) 0)), ((float) 0));
        Assertions.assertEquals((double) 0, ClassHelper.unboxed(Double.valueOf((double) 0)), ((double) 0));
    }

    @Test
    void testGetSize() {
        Assertions.assertEquals(0, ClassHelper.getSize(null));
        List<Integer> list = new ArrayList<>();
        list.add(1);
        Assertions.assertEquals(1, ClassHelper.getSize(list));
        Map map = new HashMap();
        map.put(1, 1);
        Assertions.assertEquals(1, ClassHelper.getSize(map));
        int[] array = new int[1];
        Assertions.assertEquals(1, ClassHelper.getSize(array));
        Assertions.assertEquals(-1, ClassHelper.getSize(new Object()));
    }

    @Test
    void testToUri() {
        Assertions.assertThrows(RuntimeException.class, () -> ClassHelper.toURI("#xx_abc#hello"));
    }


    @Test
    public void testGetGenericClass() {
        // 获取泛型
        Assertions.assertTrue(TypeVariable.class.isAssignableFrom(ClassHelper.getGenericClass(GenericClass.class)));
        Assertions.assertTrue(String.class.isAssignableFrom(ClassHelper.getGenericClass(GenericClass0.class)));
        Assertions.assertTrue(Collection.class.isAssignableFrom(ClassHelper.getGenericClass(GenericClass1.class)));
        Assertions.assertTrue(TypeVariable.class.isAssignableFrom(ClassHelper.getGenericClass(GenericClass2.class)));
        Assertions.assertTrue(GenericArrayType.class.isAssignableFrom(ClassHelper.getGenericClass(GenericClass3.class)));
    }

    @Test
    public void testGetSizeMethod() {
        Assertions.assertEquals("getLength()", ClassHelper.getSizeMethod(GenericClass3.class));
    }

    @Test
    public void testGetSimpleClassName() {
        Assertions.assertNull(ClassHelper.getSimpleClassName(null));
        Assertions.assertEquals("Map", ClassHelper.getSimpleClassName(Map.class.getName()));
        Assertions.assertEquals("Map", ClassHelper.getSimpleClassName(Map.class.getSimpleName()));
    }
    
    /*  泛型获取相关 */
    private interface GenericInterface<T> {
    }

    private class GenericClass<T> implements GenericInterface<T> {
    }

    private class GenericClass0 implements GenericInterface<String> {
    }

    private class GenericClass1 implements GenericInterface<Collection<String>> {
    }

    private class GenericClass2<T> implements GenericInterface<T[]> {
    }

    private class GenericClass3<T> implements GenericInterface<T[][]> {
        public int getLength() {
            return -1;
        }
    }



    private class PrivateHelloServiceImpl implements HelloService {
        private PrivateHelloServiceImpl() {
        }

        @Override
        public String sayHello() {
            return "Hello world!";
        }
    }
}