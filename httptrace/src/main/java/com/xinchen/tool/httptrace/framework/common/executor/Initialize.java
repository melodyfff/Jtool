package com.xinchen.tool.httptrace.framework.common.executor;

import com.xinchen.tool.httptrace.framework.common.loader.EnhancedServiceLoader;

/**
 *
 * @see EnhancedServiceLoader#initInstance()
 * @date 2021-07-13 15:29
 */
public interface Initialize {

  /**
   * init method
   */
  void init();
}
