package com.xinchen.tool.httpclinet;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;


/**
 *
 *
 * 参考: HttpClient实战三：Spring整合HttpClient连接池 https://www.jianshu.com/p/363e3d7c235b
 *       Http请求连接池 - HttpClient 连接池          https://yq.aliyun.com/articles/294
 *
 * @author xinchen
 * @version 1.0
 * @date 19/12/2019 14:34
 */
public final class HttpClientUtil {

    private static final CloseableHttpClient CLIENT;

    private static final int RETRY = 3;

    static {
        // 如果要使用多线程,则必须使用ThreadSafeClientConnManager创建HttpClient。
        // 默认超时时间 2000ms
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        cm.setMaxTotal(100);
        // 设置默认并发数
        cm.setDefaultMaxPerRoute(10);

        CLIENT = HttpClients.custom()
                // 连接管理
                .setConnectionManager(cm)
                .setDefaultCookieStore(new BasicCookieStore())
                // 重试策略
                .setRetryHandler(new RetryHandler())
                .build();
    }

    static class RetryHandler implements HttpRequestRetryHandler {
        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            // 尝试重试3次
            if (executionCount > RETRY){
                return false;
            }
            // 如果服务器丢掉了连接，那么就重试
            if (exception instanceof NoHttpResponseException) {
                return true;
            }
            // 不要重试SSL握手异常
            if (exception instanceof SSLHandshakeException) {
                return false;
            }
            // 超时不重试
            if (exception instanceof InterruptedIOException) {
                return false;
            }
            // 目标服务器不可达
            if (exception instanceof UnknownHostException) {
                return false;
            }
            // 连接被拒绝
            if (exception instanceof ConnectTimeoutException) {
                return false;
            }
            // SSL握手异常
            if (exception instanceof SSLException) {
                return false;
            }
            // 默认不重试
            return false;
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println(CLIENT);
                try (final CloseableHttpResponse execute = CLIENT.execute(new HttpGet("http://www.baidu.com"))){
                    System.out.println(execute.getStatusLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        TimeUnit.SECONDS.sleep(10);
    }
}
