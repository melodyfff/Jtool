package com.xinchen.tool.httptrace.core.executor;

/**
 * @date 2021-07-13 15:30
 */
public interface Callback<T> {

  /**
   * Execute t.
   *
   * @return the t
   * @throws Throwable the throwable
   */
  T execute() throws Throwable;
}
