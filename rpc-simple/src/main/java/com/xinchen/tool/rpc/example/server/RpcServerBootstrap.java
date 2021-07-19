package com.xinchen.tool.rpc.example.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @date 2021-07-19 16:05
 */
class RpcServerBootstrap {
  private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerBootstrap.class);

  public static void main(String[] args) {
    LOGGER.debug("start server");
    new AnnotationConfigApplicationContext(RpcServerConfiguration.class);
  }
}
