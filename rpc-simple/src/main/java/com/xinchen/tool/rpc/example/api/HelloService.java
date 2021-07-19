package com.xinchen.tool.rpc.example.api;

/**
 * @date 2021-07-19 16:01
 */
public interface HelloService {
  String hello(String name);

  String hello(Person person);
}
