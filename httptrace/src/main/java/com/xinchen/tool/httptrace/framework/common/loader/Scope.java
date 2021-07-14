package com.xinchen.tool.httptrace.framework.common.loader;

/**
 * @date 2021-07-13 14:33
 */
public enum Scope {
  /**
   * The extension will be loaded in singleton mode
   */
  SINGLETON,
  /**
   * The extension will be loaded in multi instance mode
   */
  PROTOTYPE
}
