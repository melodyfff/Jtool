package com.xinchen.tool.httptrace.core.loader.scen;

import com.xinchen.tool.httptrace.core.loader.LoadLevel;
import com.xinchen.tool.httptrace.core.loader.Scope;

/**
 * @date 2021-07-13 16:09
 */
@LoadLevel(name = "HelloLatin", order = 3,scope = Scope.PROTOTYPE)
public class HelloLatin implements Hello{

  @Override
  public String say() {
    return "Olá.";
  }
}
