package com.xinchen.tool.httptrace.core.loader.scen;

import com.xinchen.tool.httptrace.core.loader.LoadLevel;

/**
 * @date 2021-07-13 16:09
 */
@LoadLevel(name = "HelloFrench", order = 2)
public class HelloFrench implements Hello{

  @Override
  public String say() {
    return "Bonjour";
  }
}
