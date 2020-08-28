package com.xinchen.tool.fegin.hystrix;

import feign.Feign;
import feign.gson.GsonDecoder;

/**
 *
 * 这里是直接替换使用{@link Feign#builder()},enrich功能支持Hystrix
 *
 * 复用之前的测试用例
 *
 * @author xinchen
 * @version 1.0
 * @date 28/08/2020 14:42
 */
public class HystrixCapabilityTest extends HystrixBuilderTest {

    @Override
    protected TestInterface target() {
        return Feign.builder()
                .addCapability(
                        new HystrixCapability()
                                // 设置默认fallback
                                .fallback(TestInterface.class, new FallbackTestInterface()))
                // 设置返回结果解码器
                .decoder(new GsonDecoder())
                .target(TestInterface.class, "http://localhost:" + server.getPort());
    }


    @Override
    protected <E> E target(Class<E> api, String url) {
        return Feign.builder()
                .addCapability(
                        new HystrixCapability())
                .target(api, url);
    }

    @Override
    protected <E> E target(Class<E> api, String url, E fallback) {
        return Feign.builder()
                .addCapability(new HystrixCapability()
                        .fallback(api, fallback))
                .target(api, url);
    }

    @Override
    protected TestInterface targetWithoutFallback() {
        return Feign.builder()
                .addCapability(
                        new HystrixCapability())
                .decoder(new GsonDecoder())
                .target(TestInterface.class, "http://localhost:" + server.getPort());
    }

}
