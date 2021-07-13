package com.xinchen.tool.httptrace.spring.actuator;

import org.springframework.boot.actuate.autoconfigure.trace.http.HttpTraceAutoConfiguration;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * 官网文档： https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/html/production-ready-features.html#production-ready-endpoints
 *
 * 从springboot 2.2.0.M3开始，httptrace默认禁止： https://github.com/spring-projects/spring-boot/issues/15039
 * 需要声明一个{@link HttpTraceRepository}
 *
 *
 * @see HttpTraceAutoConfiguration
 * @see HttpTraceFilter
 *
 */
@Configuration
class HttpTraceRepositoryConfig {
  @Bean
  public HttpTraceRepository httpTraceRepository() {
    // 默认保存100次请求
    return new InMemoryHttpTraceRepository();
  }
}
