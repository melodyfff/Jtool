package com.xinchen.tool.serialization.objenesis;


import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xin chen
 * @version 1.0.0
 * @date 2021/3/1 13:23
 */
class MockObjectTest {

    @Test
    void init_with_class(){
        Exception exception = assertThrows(
                InstantiationException.class,
                () -> MockObject.class.newInstance()
        );
        assertTrue(exception.getCause().toString().contains("NoSuchMethodException"));
    }

    @Test
    void init_with_objenesis(){
        // or ObjenesisSerializer()
        Objenesis objenesis = new ObjenesisStd();
        MockObject mockObject = objenesis.newInstance(MockObject.class);
        assertEquals(mockObject.getId(), 0L);
    }

    @Test
    void init_with_objenesis_cache(){
        // 实际为<String,ObjectInstantiator>的map，存储初始化的构造器
        // or ObjenesisSerializer()
        Objenesis objenesis = new ObjenesisStd(true);
        MockObject mockObject = objenesis.newInstance(MockObject.class);
        assertEquals(mockObject.getId(), 0L);
    }

    @Test
    void init_with_ObjectInstantiator_Multithreading(){
        // or ObjenesisSerializer()
        Objenesis objenesis = new ObjenesisStd();
        // 多线程情况下使用
        ObjectInstantiator<MockObject> instantiatorOf = objenesis.getInstantiatorOf(MockObject.class);

        MockObject mockObject1 = instantiatorOf.newInstance();
        MockObject mockObject2 = instantiatorOf.newInstance();
        MockObject mockObject3 = instantiatorOf.newInstance();
    }
}