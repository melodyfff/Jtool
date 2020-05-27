package com.xinchen.tool.httptrace;

import org.springframework.boot.Banner;
import org.springframework.boot.actuate.autoconfigure.trace.http.HttpTraceAutoConfiguration;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

/**
 *
 *
 * 官网文档： https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/html/production-ready-features.html#production-ready-endpoints
 *
 * 从springboot 2.2.0.M3开始，httptrace默认禁止： https://github.com/spring-projects/spring-boot/issues/15039
 * 需要声明一个{@link HttpTraceRepository}
 *
 *
 * @see HttpTraceAutoConfiguration
 * @see HttpTraceFilter
 * @author xinchen
 * @version 1.0
 * @date 27/05/2020 11:08
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(App.class)
                .bannerMode(Banner.Mode.OFF)
                .run();
    }

    @Bean
    public HttpTraceRepository httpTraceRepository() {
        // 默认保存100次请求
        return new InMemoryHttpTraceRepository();
    }
}
