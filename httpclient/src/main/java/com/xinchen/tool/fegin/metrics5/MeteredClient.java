package com.xinchen.tool.fegin.metrics5;

import feign.Client;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import io.dropwizard.metrics5.MetricRegistry;
import io.dropwizard.metrics5.Timer.Context;

import java.io.IOException;

/**
 * Warp feign {@link Client} with metrics.
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/8/3 23:59
 */
public class MeteredClient implements Client {

    private final Client client;
    private final MetricRegistry metricRegistry;
    private final FeignMetricName metricName;
    private final MetricSuppliers metricSuppliers;

    public MeteredClient(Client client, MetricRegistry metricRegistry,
                         MetricSuppliers metricSuppliers) {
        this.client = client;
        this.metricRegistry = metricRegistry;
        this.metricSuppliers = metricSuppliers;
        this.metricName = new FeignMetricName(Client.class);
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        final RequestTemplate template = request.requestTemplate();
        try (final Context classTimer =
                     metricRegistry.timer(
                             metricName.metricName(template.methodMetadata(), template.feignTarget()),
                             metricSuppliers.timers()).time()) {
            return client.execute(request, options);
        }
    }

}
