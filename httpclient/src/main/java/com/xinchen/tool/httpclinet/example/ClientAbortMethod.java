package com.xinchen.tool.httpclinet.example;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 本示例演示如何在正常完成之前中止HTTP方法。
 *
 * This example demonstrates how to abort an HTTP method before its normal completion.
 */
public class ClientAbortMethod {

    public final static void main(String[] args) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet("http://httpbin.org/get");

            System.out.println("Executing request " + httpget.getURI());
            try (CloseableHttpResponse response = httpclient.execute(httpget)) {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                // Do not feel like reading the response body Call abort on the request object
                httpget.abort();
            }
        }
    }

}

