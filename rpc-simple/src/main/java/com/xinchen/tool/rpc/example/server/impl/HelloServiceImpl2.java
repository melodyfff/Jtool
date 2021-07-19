package com.xinchen.tool.rpc.example.server.impl;


import com.xinchen.tool.rpc.core.server.RpcService;
import com.xinchen.tool.rpc.example.api.HelloService;
import com.xinchen.tool.rpc.example.api.Person;

@RpcService(value = HelloService.class, version = "sample.hello2")
public class HelloServiceImpl2 implements HelloService {

    @Override
    public String hello(String name) {
        return "你好! " + name;
    }

    @Override
    public String hello(Person person) {
        return "你好! " + person.getFirstName() + " " + person.getLastName();
    }
}
