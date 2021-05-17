package com.xinchen.tool.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 *
 */
class CamelTimer {

  public static void main(String[] args) throws Exception {
    try(CamelContext camelContext = new DefaultCamelContext()) {
      camelContext.addRoutes(new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          from("timer:testTimer?period=2s")
              .setBody()
              .simple("Current time is ${header.firedTime}")
              .to("stream:out")
              .log("Hello Camel");
        }
      });

      camelContext.start();
      // so run for 10 seconds
      Thread.sleep(10_000);
      camelContext.stop();
    }
  }

}
