package com.xinchen.tool.httpclinet.client.factory;

import com.xinchen.tool.httpclinet.client.retry.DefaultRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;


/**
 *
 * 创建支持多线程的带cookie存储的{@link CloseableHttpClient}
 * 通过 {@link CookieThreadHttpClientFactory#getClient()} 获取 {@link CloseableHttpClient}
 * 通过 {@link CookieThreadHttpClientFactory#create()} 创建新的 {@link CloseableHttpClient}
 * @author xinchen
 * @version 1.0
 * @date 20/12/2019 14:11
 */
public class CookieThreadHttpClientFactory extends BaseCloseableHttpClientFactory {

    public CookieThreadHttpClientFactory(){
        super();
    }

    /**
     * 创建支持多线程的带cookie存储的{@link CloseableHttpClient}
     *
     * @return {@link CloseableHttpClient}
     */
    @Override
    protected CloseableHttpClient create() {
        // 如果要使用多线程,则必须使用ThreadSafeClientConnManager创建HttpClient。
        // 默认超时时间 2000ms
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        cm.setMaxTotal(100);
        // 设置默认并发数
        cm.setDefaultMaxPerRoute(100);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                // 默认 -1
                .setConnectTimeout(1000)
                // 默认 -1
                .setConnectionRequestTimeout(1000)
                // 默认 -1
                .setSocketTimeout(1000)
                .setExpectContinueEnabled(true)
                .build();

        return HttpClients.custom()
                // 连接管理
                .setConnectionManager(cm)
                // cookie设置
                .setDefaultCookieStore(new BasicCookieStore())
                // 默认重试策略
                .setRetryHandler(new DefaultRetryHandler())
                // 请求超时等设置
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
    }
}
