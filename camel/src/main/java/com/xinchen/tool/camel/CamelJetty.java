package com.xinchen.tool.camel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMessage;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * curl 'http://127.0.0.1:8280/ccc?asd=asd' -H 'Content-Type: application/json' -d '{"ok":"ok"}'
 */
class CamelJetty {

  public static void main(String[] args) throws Exception {

    CamelContext context = new DefaultCamelContext();
    context.start();

    // dynamic add route
    context.addRoutes(new Hello());

    // hold main thread
    synchronized (Hello.class){
      Hello.class.wait();
    }
  }

  static class Hello extends RouteBuilder{

    @Override
    public void configure() throws Exception {
      from("jetty:http://127.0.0.1:8280/ccc")
          // 处理http请求，并原样添加返回
          .process(new HttpProcessor())
          // 打印日志
          .to("log:helloWorld?showExchangeId=true");
    }

    static class HttpProcessor implements Processor{

      @Override
      public void process(Exchange exchange) throws Exception {
        if (exchange.getIn() instanceof HttpMessage){
          HttpMessage in = (HttpMessage) exchange.getIn();
          try(InputStream body = (InputStream) in.getBody()) {
            // 分析提取流中的数据
            String inContent = analysisMessage(body);
            // 响应返回
            if (exchange.getPattern() == ExchangePattern.InOut){
              Message message = exchange.getMessage();
              message.setBody(inContent+" || out");
            }
          }
        }

      }

      /**
       * 从stream中分析字符串内容
       *
       * @param bodyStream InputStream
       * @return String
       */
      private String analysisMessage(InputStream bodyStream) throws IOException {
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()){
          byte[] contextBytes = new byte[4096];
          int realLen;
          while ((realLen = bodyStream.read(contextBytes, 0, 4096)) != -1) {
            outStream.write(contextBytes, 0, realLen);
          }
          return new String(outStream.toByteArray(), StandardCharsets.UTF_8);
        }
      }
    }
  }
}
