package com.xinchen.tool.httptrace.framework.seata.discovery.custom;

import io.seata.common.loader.EnhancedServiceLoader;
import io.seata.common.loader.LoadLevel;
import io.seata.common.util.StringUtils;
import io.seata.config.ConfigurationFactory;
import io.seata.discovery.registry.RegistryProvider;
import io.seata.discovery.registry.RegistryService;
import io.seata.discovery.registry.RegistryType;
import java.util.stream.Stream;

/**
 * @date 2021-07-14 15:02
 */
@LoadLevel(name="Custom")
public class CustomRegistryProvider implements RegistryProvider {
  private static final String FILE_CONFIG_KEY_PREFIX = "registry.custom.name";

  private final String customName;

  public CustomRegistryProvider() {
    String name = ConfigurationFactory.CURRENT_FILE_INSTANCE.getConfig(FILE_CONFIG_KEY_PREFIX);
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("name value of custom registry type must not be blank");
    }
    if (Stream.of(RegistryType.values())
        .anyMatch(ct -> ct.name().equalsIgnoreCase(name))) {
      throw new IllegalArgumentException(String.format("custom registry type name %s is not allowed", name));
    }
    customName = name;
  }

  @Override
  public RegistryService provide() {
    return EnhancedServiceLoader.load(RegistryProvider.class, customName).provide();
  }
}
