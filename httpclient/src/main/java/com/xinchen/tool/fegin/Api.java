package com.xinchen.tool.fegin;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.xinchen.tool.fegin.hystrix.HystrixFeign;
import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 *
 * Api builder by {@link HystrixFeign#builder()}
 *
 * @author xinchen
 * @version 1.0
 * @date 08/09/2020 14:43
 */
class Api {

    private static final Logger log = LoggerFactory.getLogger(Api.class);

    /**
     * Private Construct
     */
    private Api(){}

    public static <T> Builder<T> builder(Class<T> apiClass, String host){
        return new Builder<>(apiClass, host);
    }

    public static class Builder<T> {

        private static final String JSON_CONTENT_TYPE = "application/json;charset=utf-8";
        private final Class<T> apiClazz;
        private final String host;
        /**请求参数设置： 超时时间、跳转*/
        private Request.Options requestOptions = new Request.Options(10 , TimeUnit.SECONDS, 60 ,TimeUnit.SECONDS,true);


        public Builder(Class<T> apiClazz, String host) {
            this.apiClazz = apiClazz;
            this.host = host;
        }

        public T build() {
            return HystrixFeign.builder()
                    // 请求拦截器，添加header
                    .requestInterceptor((requestTemplate)->{
                        requestTemplate.header(HTTP.CONTENT_TYPE, JSON_CONTENT_TYPE);
                    })
                    .encoder(new GsonEncoder())
                    .decoder(new GsonDecoder())
                    // handle error
                    .errorDecoder(new ErrorDecoder.Default())
                    .logger(new feign.Logger.ErrorLogger())
                    .logLevel(feign.Logger.Level.BASIC)
                    .setterFactory((target, method) -> HystrixCommand.Setter
                            .withGroupKey(HystrixCommandGroupKey.Factory.asKey(target.name()))
                            .andCommandKey(HystrixCommandKey.Factory.asKey(Feign.configKey(target.type(), method)))
                            .andCommandPropertiesDefaults(
                                    HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(
                                            requestOptions.readTimeoutMillis()
                                    )
                            ))
                    // 关闭重试
                    .retryer(Retryer.NEVER_RETRY)
                    .target(apiClazz, host);
        }
    }
}
