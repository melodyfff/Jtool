package com.xinchen.tool.httptrace.framework.seata.discovery.custom;

import io.seata.config.ConfigChangeListener;
import io.seata.discovery.registry.RegistryService;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @date 2021-07-14 15:09
 */
class CustomRegistryServiceForTest implements RegistryService<ConfigChangeListener>  {

  @Override
  public void register(InetSocketAddress address) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void unregister(InetSocketAddress address) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void subscribe(String cluster, ConfigChangeListener listener) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void unsubscribe(String cluster, ConfigChangeListener listener) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<InetSocketAddress> lookup(String key) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() throws Exception {
    throw new UnsupportedOperationException();
  }
}
