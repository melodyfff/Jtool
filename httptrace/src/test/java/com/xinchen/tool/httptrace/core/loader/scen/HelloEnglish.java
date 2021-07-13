package com.xinchen.tool.httptrace.core.loader.scen;

import com.xinchen.tool.httptrace.core.loader.LoadLevel;

/**
 * @date 2021-07-13 16:09
 */
@LoadLevel(name = "HelloEnglish", order = 1)
public class HelloEnglish implements Hello{

  @Override
  public String say() {
    return "hello!";
  }
}
