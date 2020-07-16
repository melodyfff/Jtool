package com.xinchen.tool.httpclinet.client.factory;

import org.apache.http.impl.client.CloseableHttpClient;

/**
 *
 * 基类 HttpClientFactory
 *
 * see {@link CookieThreadHttpClientFactory}
 *
 * @author xinchen
 * @version 1.0
 * @date 20/12/2019 13:49
 */
public abstract class BaseCloseableHttpClientFactory implements AppHttpClientProvider {
    protected final CloseableHttpClient client;

    BaseCloseableHttpClientFactory() {
        client = create();
    }

    /**
     * 创建{@link CloseableHttpClient}
     * @return {@link CloseableHttpClient}
     */
    protected abstract CloseableHttpClient create();


    @Override
    public CloseableHttpClient getClient(){
        return this.client;
    }
}
