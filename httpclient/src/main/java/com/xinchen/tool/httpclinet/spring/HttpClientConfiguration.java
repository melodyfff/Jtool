package com.xinchen.tool.httpclinet.spring;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * see https://github.com/li5454yong/springboot-httpclient/
 * @author xinchen
 * @version 1.0
 * @date 20/12/2019 16:19
 */
@Configuration
@PropertySource(value = {"classpath:httpclient.properties"})
public class HttpClientConfiguration {
    @Value("${http.maxTotal}")
    private Integer maxTotal;

    @Value("${http.defaultMaxPerRoute}")
    private Integer defaultMaxPerRoute;

    @Value("${http.connectTimeout}")
    private Integer connectTimeout;

    @Value("${http.connectionRequestTimeout}")
    private Integer connectionRequestTimeout;

    @Value("${http.socketTimeout}")
    private Integer socketTimeout;

    @Value("${http.validateAfterInactivity}")
    private Integer validateAfterInactivity;


    /**
     * 实例化一个连接池管理器，设置最大连接数、并发连接数
     * @return PoolingHttpClientConnectionManager
     */
    @Bean
    public PoolingHttpClientConnectionManager httpClientConnectionManager(){
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // 最大连接数
        cm.setMaxTotal(maxTotal);
        // 并发数
        cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
        cm.setValidateAfterInactivity(validateAfterInactivity);
        return cm;
    }


    /**
     * 注入连接池，用于获取httpClient
     * @param httpClientConnectionManager PoolingHttpClientConnectionManager
     *
     * @return CloseableHttpClient
     */
    @Bean
    public CloseableHttpClient closeableHttpClient(PoolingHttpClientConnectionManager httpClientConnectionManager){
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(httpClientConnectionManager);
        return httpClientBuilder.build();
    }

    /**
     * 主要是发起 get/post请求时设置默认的超时时间等
     * @return RequestConfig
     */
    @Bean
    public RequestConfig getRequestConfig(){
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder.setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .build();
    }


}

