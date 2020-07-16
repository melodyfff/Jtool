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
        // 检查空闲默认2000ms
        cm.setValidateAfterInactivity(2000);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                // 默认 -1 ms
                .setConnectTimeout(5000)
                // 默认 -1 ms
                .setConnectionRequestTimeout(5000)
                // 默认 -1 ms
                .setSocketTimeout(5000)
                .setExpectContinueEnabled(true)
                .build();

        return innerCreate(cm,defaultRequestConfig);
    }

    /**
     * 创建支持多线程的带cookie存储的{@link CloseableHttpClient}
     * @param cm PoolingHttpClientConnectionManager
     * @param requestConfig RequestConfig
     * @return CloseableHttpClient
     */
    public CloseableHttpClient create(PoolingHttpClientConnectionManager cm,
                                      RequestConfig requestConfig) {
        return innerCreate(cm, requestConfig);
    }

    /**
     * 内部实际创建类
     * @param cm PoolingHttpClientConnectionManager
     * @param requestConfig RequestConfig
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient innerCreate(PoolingHttpClientConnectionManager cm,
                                            RequestConfig requestConfig){
        return HttpClients.custom()
                // 连接管理
                .setConnectionManager(cm)
                // cookie设置
                .setDefaultCookieStore(new BasicCookieStore())
                // 默认重试策略
                .setRetryHandler(new DefaultRetryHandler())
//                .setConnectionManagerShared(true)
                // 请求超时等设置
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}
