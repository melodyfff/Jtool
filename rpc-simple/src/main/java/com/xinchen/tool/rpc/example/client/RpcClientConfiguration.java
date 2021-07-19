package com.xinchen.tool.rpc.example.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @date 2021-07-19 16:09
 */
@Configuration
@PropertySource(value = "example/rpc.properties")
class RpcClientConfiguration {
  @Value("${rpc.service.address}")
  private String serviceAddress;
  @Value("${rpc.registry.address}")
  private String registryAddress;



}
