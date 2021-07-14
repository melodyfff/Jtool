package com.xinchen.tool.httptrace.framework.seata.discovery.custom;

import static org.junit.jupiter.api.Assertions.*;

import io.seata.common.loader.LoadLevel;
import io.seata.discovery.registry.RegistryProvider;
import io.seata.discovery.registry.RegistryService;

/**
 * @date 2021-07-14 15:05
 */
@LoadLevel(name = "forTest")
public class CustomRegistryProviderForTest implements RegistryProvider {

  @Override
  public RegistryService provide() {
    return new CustomRegistryServiceForTest();
  }
}