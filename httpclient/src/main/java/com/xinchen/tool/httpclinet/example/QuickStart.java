package com.xinchen.tool.httpclinet.example;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * QuickStart
 * <p>
 * Reference: http://hc.apache.org/httpcomponents-client-4.5.x/quickstart.html
 *
 * @author xinchen
 * @version 1.0
 * @date 30/10/2019 09:20
 */
public class QuickStart {
    public static void main(String[] args) throws IOException {
        // init httpclient
        CloseableHttpClient httpClient = HttpClients.createDefault();


        HttpGet httpGet = new HttpGet("https://www.bing.com");
        // http连接被response object持有, 允许直接从network socket流式(stream)传输响应内容
        // 为了正确释放资源,用户必须在finally中调用CloseableHttpResponse#close()
        // 注意: 如果响应内容没有完全被消耗,则无法安全的重用连接,连接将被connection manager关闭并且丢弃
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            // do something with teh response body and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }


        // The fluent API
        System.out.println(
                Optional.of(Request.Get("https://www.bing.com")
                .execute()
                .returnContent().toString()
                ).orElse(null)
        );


        HttpPost httpPost = new HttpPost("https://login.live.com/login.srf");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("username", "vip"));
        nvps.add(new BasicNameValuePair("password", "secret"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response2 = httpClient.execute(httpPost);
        try {
            System.out.println(response2.getStatusLine());
            HttpEntity entity2 = response2.getEntity();
            // do something with teh response body and ensure it is fully consumed
            EntityUtils.consume(entity2);
        } finally {
            response2.close();
        }

        // The fluent API
        System.out.println(
                Optional.of(Request.Post("https://login.live.com/login.srf")
                        .bodyForm(Form.form().add("username","vip").add("password","secret").build())
                        .execute()
                        .returnContent().toString()
                ).orElse(null)
        );
    }
}
