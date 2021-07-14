package com.xinchen.tool.httptrace.framework.seata.discovery.custom;

import io.seata.discovery.registry.RegistryFactory;
import io.seata.discovery.registry.RegistryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @date 2021-07-14 15:11
 */
class CustomRegistryTest {
  @Test
  void testCustomRegistryLoad() {
    RegistryService registryService = RegistryFactory.getInstance();
    Assertions.assertTrue(registryService instanceof CustomRegistryServiceForTest);
  }
}
