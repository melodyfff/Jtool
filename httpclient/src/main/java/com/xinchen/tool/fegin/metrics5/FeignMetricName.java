package com.xinchen.tool.fegin.metrics5;

import feign.MethodMetadata;
import feign.Target;
import io.dropwizard.metrics5.MetricName;
import io.dropwizard.metrics5.MetricRegistry;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/8/3 23:43
 */
public class FeignMetricName {
    private final Class<?> meteredComponent;

    public FeignMetricName(Class<?> meteredComponent) {
        this.meteredComponent = meteredComponent;
    }

    public MetricName metricName(MethodMetadata methodMetadata, Target<?> target, String suffix) {
        return metricName(methodMetadata, target)
                .resolve(suffix);
    }

    public MetricName metricName(MethodMetadata methodMetadata, Target<?> target) {
        return metricName(methodMetadata.targetType(), methodMetadata.method(), target.url());
    }

    public MetricName metricName(Class<?> targetType, Method method, String url) {
        return MetricRegistry.name(meteredComponent)
                .tagged("client", targetType.getName())
                .tagged("method", method.getName())
                .tagged("host", extractHost(url));
    }

    private String extractHost(final String targetUrl) {
        try {
            return new URI(targetUrl).getHost();
        } catch (final URISyntaxException e) {
            // can't get the host, in that case, just read first 20 chars from url
            return targetUrl.length() <= 20
                    ? targetUrl
                    : targetUrl.substring(0, 20);
        }
    }
}
