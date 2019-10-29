package com.xinchen.tool.httpclinet;

import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");
        System.out.println(Request.Get("http://www.baidu.com").execute().returnContent().asString(StandardCharsets.UTF_8));
    }
}
