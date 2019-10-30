package com.xinchen.tool.httpclinet.example;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 此示例演示了在手动处理HTTP响应的情况下如何确保将基础HTTP连接释放回连接管理器。
 *
 * This example demonstrates the recommended way of using API to make sure
 * the underlying connection gets released back to the connection manager.
 */
public class ClientConnectionRelease {

    public final static void main(String[] args) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet("http://httpbin.org/get");

            System.out.println("Executing request " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());

                // Get hold of the response entity
                HttpEntity entity = response.getEntity();

                // 如果响应未包含实体entity,则无需担心连接释放
                if (entity != null) {
                    InputStream inStream = entity.getContent();
                    try {
                        inStream.read();
                        // do something useful with the response
                    } catch (IOException ex) {
                        // 如果发生了IOException,则连接将被释放自动返回到连接管理器
                        throw ex;
                    } finally {
                        // Closing the input stream will trigger connection release
                        inStream.close();
                    }
                }
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

}

