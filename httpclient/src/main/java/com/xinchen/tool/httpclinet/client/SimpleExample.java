package com.xinchen.tool.httpclinet.client;

import com.xinchen.tool.httpclinet.client.factory.AppHttpClientProvider;
import com.xinchen.tool.httpclinet.client.factory.CookieThreadHttpClientFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xinchen
 * @version 1.0
 * @date 20/12/2019 14:42
 */
public class SimpleExample {
    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());


        AppHttpClientProvider provider = new CookieThreadHttpClientFactory();
        final CloseableHttpClient client = provider.getClient();


        Callable<Void> task = () -> {
            System.out.println(String.format("%s is running , Client %s",Thread.currentThread().getName(),client));
            try (final CloseableHttpResponse response = client.execute(new HttpGet("http://www.baidu.com"))){
                System.out.println(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        };


        for (int i = 0; i < 100; i++) {
            executorService.submit(task);
        }

        executorService.shutdown();
    }
}
