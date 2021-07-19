package com.xinchen.tool.rpc.example.server;

import com.xinchen.tool.rpc.core.registry.ServiceRegistry;
import com.xinchen.tool.rpc.core.registry.zookeeper.ZooKeeperServiceRegistry;
import com.xinchen.tool.rpc.core.server.RpcServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @date 2021-07-19 16:09
 */
@Configuration
@PropertySource(value = "example/rpc.properties")
@ComponentScan(basePackageClasses={RpcServerConfiguration.class})
class RpcServerConfiguration {
  @Value("${rpc.service.address}")
  private String serviceAddress;
  @Value("${rpc.registry.address}")
  private String registryAddress;

  @Bean
  ServiceRegistry serviceRegistry(){
    return new ZooKeeperServiceRegistry(registryAddress);
  }

  @Bean
  RpcServer prcServer(ServiceRegistry serviceRegistry){
    return new RpcServer(serviceAddress,serviceRegistry);
  }
}
