package com.xinchen.tool.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.concurrent.TimeUnit;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/12/12 17:55
 */
public class OkHttpClientFactory {
    public static OkHttpClient createDnsResolve(){
        return new OkHttpClient.Builder()
                // 默认值 10s
                .connectTimeout(10, TimeUnit.SECONDS)
                .dns(DnsFacory.create())
                .addInterceptor((chain -> {
                    Request request = chain.request();
                    return chain.proceed(request);
                }))
                .build();
    }

    public static OkHttpClient create(){
        return new OkHttpClient.Builder()
                // 默认值 10s
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
    }
}
