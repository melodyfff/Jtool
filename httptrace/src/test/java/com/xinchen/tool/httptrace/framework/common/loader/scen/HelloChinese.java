package com.xinchen.tool.httptrace.framework.common.loader.scen;

import com.xinchen.tool.httptrace.framework.common.loader.LoadLevel;

/**
 * @date 2021-07-13 16:09
 */
@LoadLevel(name = "HelloChinese", order = Integer.MIN_VALUE)
public class HelloChinese implements Hello{

  @Override
  public String say() {
    return "你好";
  }
}
