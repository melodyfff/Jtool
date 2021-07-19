package com.xinchen.tool.rpc.example.server.impl;

import com.xinchen.tool.rpc.core.server.RpcService;
import com.xinchen.tool.rpc.example.api.HelloService;
import com.xinchen.tool.rpc.example.api.Person;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
