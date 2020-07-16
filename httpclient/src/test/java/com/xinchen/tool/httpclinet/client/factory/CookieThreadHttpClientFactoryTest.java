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
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;


/**
 * 注意： Timeout waiting for connection from pool ?
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/7/15 23:09
 */
public class CookieThreadHttpClientFactoryTest {
    private static final int COUNT = 1000;

    @Test
    public void multiThread() throws IOException, InterruptedException, BrokenBarrierException {
        final CookieThreadHttpClientFactory factory = new CookieThreadHttpClientFactory();
        AtomicInteger count = new AtomicInteger(0);
        CyclicBarrier barrier = new CyclicBarrier(COUNT + 1);
        Runnable task = () -> {
            try {
                final HttpGet httpGet = new HttpGet("http://www.baidu.com/");
                // 这两个地方考虑是否释放? 已经交给连接池管理，关闭操作连接池进行即可
                final CloseableHttpClient client = factory.getClient();
                // response需要注意关闭连接,不然会一直hold client不放回池中
                // 因为并发控制默认设置了100，会一直卡住，不往下走
                try (final CloseableHttpResponse response = client.execute(httpGet)) {
                    count.incrementAndGet();
                } finally {
                    barrier.await();
                }
            } catch (IOException | InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
                System.out.println(">>>>>> Error " + e.getMessage());
            }
        };

        final ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < COUNT; i++) {
            executorService.execute(task);
        }

        // 控制等待所有线程执行完毕
        while (barrier.getNumberWaiting() < COUNT) {
            Thread.yield();
        }
        barrier.await();
        Thread.sleep(3000);
        executorService.shutdown();

        // 判断和预期执行的是否相同
        assertEquals(COUNT, count.get());
        System.out.println("Main done.");
    }

}