package com.xinchen.tool.fegin.metrics5;

import feign.Feign;
import feign.RequestLine;
import feign.mock.HttpMethod;
import feign.mock.MockClient;
import feign.mock.MockTarget;
import io.dropwizard.metrics5.MetricRegistry;
import io.dropwizard.metrics5.SharedMetricRegistries;
import org.junit.Test;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.*;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/8/4 0:08
 */
public class Metrics5CapabilityTest {
    interface SimpleSource {
        @RequestLine("GET /get")
        String get(String body);
    }

    @Test
    public void addMetricsCapability() {
        // 指标
        final MetricRegistry registry = SharedMetricRegistries.getOrCreate("unit_test");

        // 创建要监控的source
        final SimpleSource source = Feign.builder()
                .client(new MockClient().ok(HttpMethod.GET, "/get", "123"))
                .addCapability(new Metrics5Capability(registry))
                .target(new MockTarget<>(SimpleSource.class));

        source.get("0x123");

        // 预计有6个指标
        assertThat(registry.getMetrics(), aMapWithSize(6));

        // 判断每个指标中的client一致
        registry.getMetrics().keySet().forEach(metricName -> assertThat(
                "Expect all metric names to include client name:" + metricName,
                metricName.getTags(),
                hasEntry("client", "com.xinchen.tool.fegin.metrics5.Metrics5CapabilityTest$SimpleSource")));

        registry.getMetrics().keySet().forEach(metricName -> assertThat(
                "Expect all metric names to include method name:" + metricName,
                metricName.getTags(),
                hasEntry("method", "get")));

        registry.getMetrics().keySet().forEach(metricName -> assertThat(
                "Expect all metric names to include host name:" + metricName,
                metricName.getTags(),
                // hostname is null due to feign-mock shortfalls
                hasEntry("host", null)));
    }
}