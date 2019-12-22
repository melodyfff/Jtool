package com.xinchen.tool.httpclinet.spring;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * 简单示例
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/12/22 15:10
 */
public class Client {
    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(HttpClientConfiguration.class);

        CloseableHttpClient client = context.getBean(CloseableHttpClient.class);
        RequestConfig requestConfig = context.getBean(RequestConfig.class);

        HttpGet httpGet = new HttpGet("http://www.baidu.com");
        httpGet.setConfig(requestConfig);

        System.out.println(client.execute(httpGet));
    }
}
