package com.xinchen.tool.httpclinet.example;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 一个简单的示例，使用HttpClient执行针对需要用户认证的目标站点。
 * A simple example that uses HttpClient to execute an HTTP request against a target site that requires user authentication.
 */
public class ClientAuthentication {

    public static void main(String[] args) throws Exception {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope("httpbin.org", 80),
                new UsernamePasswordCredentials("user", "passwd"));
        try (CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build()) {
            HttpGet httpget = new HttpGet("http://httpbin.org/basic-auth/user/passwd");

            System.out.println("Executing request " + httpget.getRequestLine());
            try (CloseableHttpResponse response = httpclient.execute(httpget)) {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(response.getEntity()));
            }
        }
    }
}
