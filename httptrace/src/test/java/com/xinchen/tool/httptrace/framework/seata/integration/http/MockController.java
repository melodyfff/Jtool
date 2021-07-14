package com.xinchen.tool.httptrace.framework.seata.integration.http;

import io.seata.core.context.RootContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @date 2021-07-14 13:39
 */
@Controller
class MockController {
  @RequestMapping("/testGet")
  @ResponseBody
  public String testGet(HttpTest.Person person) {
    /* verify xid propagate by test case */
    Assertions.assertEquals(HttpTest.XID, RootContext.getXID());
    return person.toString();
  }


  @ResponseBody
  @PostMapping("/testPost")
  public String testPost(@RequestBody HttpTest.Person person) {
    /* verify xid propagate by test case */
    Assertions.assertEquals(HttpTest.XID,RootContext.getXID());
    return person.toString();
  }

  @RequestMapping("/testException")
  @ResponseBody
  public String testException(HttpTest.Person person) {
    throw new RuntimeException();
  }
}
