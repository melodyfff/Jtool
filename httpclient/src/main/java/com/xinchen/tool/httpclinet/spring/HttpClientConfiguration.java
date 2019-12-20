package com.xinchen.tool.httpclinet.spring;

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

    @Value("${http.staleConnectionCheckEnabled}")
    private boolean staleConnectionCheckEnabled;


    @Bean
    public PoolingHttpClientConnectionManager httpClientConnectionManager(){
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // 最大连接数
        cm.setMaxTotal(maxTotal);
        // 并发数
        cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return cm;
    }




}

