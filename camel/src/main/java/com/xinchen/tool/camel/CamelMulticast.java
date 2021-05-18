package com.xinchen.tool.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.MulticastDefinition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multicast 组播
 */
class CamelMulticast {
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        // start
        context.start();

        context.addRoutes(new MulticastCamelRouteBuilder());
    }

    static class MulticastCamelRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            ExecutorService executorService = Executors.newFixedThreadPool(10);

            MulticastDefinition multicastDefinition = from("jetty:http://0.0.0.0:8282/multicastCamel").multicast();

            // multicast 中的消息路由可以顺序执行也可以并发执行
            multicastDefinition.setParallelProcessing("true");
            multicastDefinition.setExecutorService(executorService);

            // 注意，multicast中各路由路径的Excahnge都是基于上一路由元素的excahnge复制而来
            // 无论前者Excahnge中的Pattern如何设置，其处理结果都不会反映在最初的Excahnge对象中
            multicastDefinition
                    .to("log:hello1?showExchangeId=true", "log:hello2?showExchangeId=true")
                    .end()
                    .process(new SomeProcessor());
        }
    }

    static class SomeProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            Message message = exchange.getMessage();

            System.out.println("SomeProcessor - exchange: " + exchange);

            InputStream body = (InputStream) message.getBody();

            if (exchange.getPattern() == ExchangePattern.InOut) {
                Message out = exchange.getMessage();
                out.setBody(analysisMessage(body) + " || SomeProcessor");
            }
        }


        /**
         * 从stream中分析字符串内容
         *
         * @param bodyStream InputStream
         * @return String
         */
        private String analysisMessage(InputStream bodyStream) throws IOException {
            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
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
