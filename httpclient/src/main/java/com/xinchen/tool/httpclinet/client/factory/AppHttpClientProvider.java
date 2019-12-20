package com.xinchen.tool.httpclinet.client.factory;

import org.apache.http.impl.client.CloseableHttpClient;

/**
 *
 * HttpClient供应商
 *
 * @author xinchen
 * @version 1.0
 * @date 20/12/2019 14:44
 */
public interface AppHttpClientProvider {
    /**
     * 获取{@link CloseableHttpClient}
     * @return {@link CloseableHttpClient}
     */
    CloseableHttpClient getClient();
}
