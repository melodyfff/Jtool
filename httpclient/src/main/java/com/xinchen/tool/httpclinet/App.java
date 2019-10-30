package com.xinchen.tool.httpclinet;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Hello world!
 *
 * HttpClient4.X 官网教程: http://hc.apache.org/httpcomponents-client-4.5.x/tutorial
 *
 */
public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");
        System.out.println(Request.Get("http://www.baidu.com").execute().returnContent().asString(StandardCharsets.UTF_8));
        System.out.println(new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK,"OK"));
    }
}
