package com.xinchen.tool.rpc.example.client;

import com.xinchen.tool.rpc.core.client.RpcProxy;
import com.xinchen.tool.rpc.core.registry.ServiceDiscovery;
import com.xinchen.tool.rpc.core.registry.zookeeper.ZooKeeperServiceDiscovery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @date 2021-07-19 16:09
 */
@Configuration
@PropertySource(value = "example/rpc.properties")
public class RpcClientConfiguration {
  @Value("${rpc.service.address}")
  private String serviceAddress;
  @Value("${rpc.registry.address}")
  private String registryAddress;


  @Bean
  ServiceDiscovery serviceDiscovery(){
    return new ZooKeeperServiceDiscovery(registryAddress);
  }

  @Bean
  RpcProxy rpcProxy(ServiceDiscovery serviceDiscovery){
    return new RpcProxy(serviceDiscovery);
  }
}
