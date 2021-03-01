package com.xinchen.tool.serialization.protostuff.usage;

/**
 * @author xin chen
 * @version 1.0.0
 * @date 2021/3/1 14:51
 */
public class Foo {
    String name;
    int id;

    public Foo(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
