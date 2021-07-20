package com.xinchen.tool.rpc.example.client.client;

import com.xinchen.tool.rpc.core.client.RpcProxy;
import com.xinchen.tool.rpc.example.api.HelloService;
import com.xinchen.tool.rpc.example.api.Person;
import com.xinchen.tool.rpc.example.client.RpcClientConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @date 2021-07-20 15:16
 */
class Client {

  public static void main(String[] args) {
    ApplicationContext context = new AnnotationConfigApplicationContext(RpcClientConfiguration.class);
    RpcProxy rpcProxy = context.getBean(RpcProxy.class);

    Person person = new Person("Jay", "Zhou");

    HelloService helloService = rpcProxy.create(HelloService.class);
    System.out.println(helloService.hello("World"));
    System.out.println(helloService.hello(person));

    HelloService helloServiceVersion = rpcProxy.create(HelloService.class, "sample.hello2");
    System.out.println(helloServiceVersion.hello("World"));
    System.out.println(helloServiceVersion.hello(person));

  }
}
