package com.xinchen.tool.httpclinet.example;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * 一个示例，演示如何使用HttpClient API执行基于表单的登录。
 *
 * 进行登录完成后,会将cookie信息存储在本地,下次请求就无需再次登录
 *
 * A example that demonstrates how HttpClient APIs can be used to perform
 * form-based logon.
 */
public class ClientFormLogin {

    public static void main(String[] args) throws Exception {
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            HttpGet httpget = new HttpGet("http://cas/login");
            CloseableHttpResponse response1 = httpclient.execute(httpget);
            try {
                HttpEntity entity = response1.getEntity();

                System.out.println("Login form get: " + response1.getStatusLine());
                EntityUtils.consume(entity);

                System.out.println("Initial set of cookies:");
                List<Cookie> cookies = cookieStore.getCookies();
                if (cookies.isEmpty()) {
                    System.out.println("None");
                } else {
                    for (int i = 0; i < cookies.size(); i++) {
                        System.out.println("- " + cookies.get(i).toString());
                    }
                }
            } finally {
                response1.close();
            }

            // 模拟登录cas
            HttpUriRequest login = RequestBuilder.post()
                    .setUri(new URI("http://cas/login"))
                    .addParameter("username", "")
                    .addParameter("password", "")
                    .addParameter("execution","3ea43466-f2d0-4464-8148-8ed5b1b3af12_ZXlKaGJHY2lPaUpJVXpVeE1pSjkuUVc4MVVTdFhOSGc1WkdVemJpdFVkMlZGT0VKV1RXTlVXSGQyZGk5NFpFeHNRbEp4WjNnMVJXMVJTVzl0WW5KQ1Z5OHpRM0JuY1ZwQmVVTkhSVzg0ZFVWa1FteDBRa2x5U0M5b2ExbHhSWGh4ZWxneVRubHdRMFZZTVUxNGRTdFhNemxrTlZsaGFrZDNhMkV5T1VKM1JGSmtOMU56V2k5T1duZDFabTFETWxvelMwTXJURU5FTDBwb1VtMVBTRXhvUlZSc1VWaFFaM05QZUZaRVMwVkVSbmR2WjB4VFVsQjJibTlFT0RWVGRYaEhTM05DTTJWNFdtbEdlRk5sT0RaQlMwRjJlVmhCVDA1VVIzQTNSMWxCY0hoVVIyUkJkR00wTWpoWmRFOTJPWGRrZERSaU1EVjFNWE5uT0ZKVGVXbFBUVVJaYjJ0NlIwcDJiRU56ZHpCNmNqQkJkRWc1TnpRMmRFdFlkbmN6UzIxS1ZVWTRlQzlCU25SUFFuZDJaekJoV25NeVFVY3lkR0puWkdGcE55dENlamRKU2xKb2JHMURkbFJxU1dOR1ptWTFjVTFsY1dGamRXRldZbWd3TVZjMWIyRjNTamRsYjBKc2NuTm5NREo2VEdJMGRtMTRlV0ZqVmxKRU1uWkZiMGt4Y1hoeUsyTnZibTFvVTNKeFVXaFVXRGhVYTJSS1JVNTNhMm92VmpkTWRXTk1XbFE0ZDBsSk9EQk9jM0ZvTkhKdFpUSXhOVVprY0RSNFYxRnRkSFJNV21aYVVYaFBkMDFHWTNoTGVGaFVaMnd5WjFWNU1ESXJVakJrY2tjekwyTkdTSGxKUVVNd2VWTm9Wa3RhTDFoV2FDOHdSUzlzZDJzM1FYSnlMMVYzVDBWUFdGVkxWRTF1ZDJVemQzRndhemRsUm5kWmF6SmtOMWxtY2pkSlpqRjBPSGRXWnpWV1ZqVnlUV1JJTlRaM1FtNTRNVEpvU1VoQ2JWa3lRMk1yVW1sNGNYWXhURmRGTjBaVVFuRldWSEJ1ZVhsSFIxWnJXWEp1Y0V4c2JtMWtWMmc1TDJjM1FsWnFWbVpxUzFwSmNYcE1VMEZXVmpoNFdEVkhhM2xRYTNOUmEyTTBaQzgwUW1OWFRGUm5TV00xYUZwbGRrSm5hMlZDZDNoTVJHWjZVV0pCT0dadVNTdGphM3B5VWsxSU4yaE9UbHBaVjFWVlpUWndhazFFWWxBcmRFeHhZVFJtY2s5RVRtbFpjRUZOWkdwbWFGRTVObVJNVm1wRlJGSktWVEI2UjB0eVp6WmxTMjFQZFRjNGMzbHZNVEJFWW1NdmRGWm9Xa3RXWlRoWVpsZ3dRMHhFTnk5U01WaEZVV2RaUmxaMlpGTlFUREZXYnpBd1dHcFVhR2hwYW01TFJtRmhiRWwyZFVZeWNWUllaMXBZWW10UVNHTndNbnBtVFhCb2JVaHRXa3BGZVdwaWFqQXdhV1JzUjBGM2FVbHpRemhVT0RsRE9EZHhkVGRYZHpsa09FUjNZMHRWYTBzNFZ6TkhhVlUxWTNVemNrZFBNbGhrTDBaeWFXWjVUV0pSUTI5d05teHNlRzlLY1ZwTVVIRm9ZMFZ6YUc1blkwbHhkMFpZY0RjemJVSnVTWGhUTlRSbU1FaGpjRFJoZG14MGJVVTRhMWxMVDBsNE9HVmtibEZaTWs1V1NYcDRNMkppZEdselZURTJUSE5WUWxaRWVrUldUREJvUVhsU1YzQkdMMVZCTkhSdVZtRXZNSFZZTUdWd1MxWTFUVk4yUjFGMFJrVk9aMVJDTUVsM1ozRldWVGtyVTBOYVduQlFhU3RNWkZVdmRsa3ZjMHRSVVhkWk5rUnBXVXB5TVhkSFJ5dFJhekpOVFc5S01qUjFTVnBsZUZaYUsxaFJiazFyTTBreVJFUXhiRmhNV201SmJ6Vk1OaTl6TkZaalIwbFpRMU5NWTB0eVFsbEdURTVYYlhKYVNVVTRSRW81VTBOSE1tVkdRMkoxVGt0TlNEbE5QUS5meTZBUW5MeG5jd2xHNWJZOVFDS2ZlcjlKcnkydUJGSzlaak15S2hsdDRxbnlnYVJ2TERYQTgtVWVMYk1ZMGNkVXkxT3llSWREbkp0ODBlbTk4TjliZw==")
                    .addParameter("_eventId","submit")
                    .build();
            CloseableHttpResponse response2 = httpclient.execute(login);
            try {
                HttpEntity entity = response2.getEntity();

                System.out.println("Login form get: " + response2.getStatusLine());
                System.out.println( EntityUtils.toString(entity));
                System.out.println("Post logon cookies:");
                List<Cookie> cookies = cookieStore.getCookies();
                if (cookies.isEmpty()) {
                    System.out.println("None");
                } else {
                    for (int i = 0; i < cookies.size(); i++) {
                        System.out.println("- " + cookies.get(i).toString());
                    }
                }

                // 访问需要登录的页面
                // CloseableHttpResponse execute = httpclient.execute(new HttpGet("http://someauthpage.com"));
                // System.out.println(EntityUtils.toString(execute.getEntity(),"utf-8"));

            } finally {
                response2.close();
            }
        } finally {
            httpclient.close();
        }
    }
}
