package com.xinchen.tool.serialization.objenesis;

/**
 *
 * 构造函数为私有，或者默认构造函数需要参数
 *
 * @author xin chen
 * @version 1.0.0
 * @date 2021/3/1 13:17
 */
public class MockObject {
    private long id;

//    private MockObject(){}

    public MockObject(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MockObject{" +
                "id=" + id +
                '}';
    }
}
