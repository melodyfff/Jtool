package com.xinchen.tool.httpclinet.client.factory;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/7/15 23:09
 */
public class CookieThreadHttpClientFactoryTest {
    static final int COUNT = 50;
    @Test
    public void create() throws IOException, InterruptedException, BrokenBarrierException {
        final CookieThreadHttpClientFactory factory = new CookieThreadHttpClientFactory();
        final HttpGet httpGet = new HttpGet("https://cn.bing.com/");
        AtomicInteger count = new AtomicInteger(0);
        CyclicBarrier barrier = new CyclicBarrier(COUNT+1);
        Runnable task = () -> {
            try {
                final CloseableHttpResponse response = factory.getClient().execute(httpGet);
                count.incrementAndGet();
                barrier.await();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(">>>>>> Error" + e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        };

        final ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < COUNT; i++) {
            executorService.execute(task);
        }
        while (barrier.getNumberWaiting()<COUNT){
            barrier.await();
        }
        executorService.shutdown();

        System.out.println(count.get());
        System.out.println("Main done.");
    }


}