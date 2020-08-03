package com.xinchen.tool.fegin.metrics5;

import feign.FeignException;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import io.dropwizard.metrics5.MetricRegistry;
import io.dropwizard.metrics5.Timer.Context;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Warp feign {@link Decoder} with metrics.
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/8/3 23:47
 */
public class MeteredDecoder implements Decoder {
    private final Decoder decoder;
    private final MetricRegistry metricRegistry;
    private final MetricSuppliers metricSuppliers;
    private final FeignMetricName metricName;

    public MeteredDecoder(Decoder decoder,
                          MetricRegistry metricRegistry,
                          MetricSuppliers metricSuppliers) {
        this.decoder = decoder;
        this.metricRegistry = metricRegistry;
        this.metricSuppliers = metricSuppliers;
        this.metricName = new FeignMetricName(Decoder.class);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        final RequestTemplate template = response.request().requestTemplate();
        final MeteredBody body = response.body() == null
                ? null
                : new MeteredBody(response.body());

        response = response.toBuilder().body(body).build();

        final Object decoded;
        try (final Context classTimer =
                     metricRegistry
                             .timer(metricName.metricName(template.methodMetadata(), template.feignTarget()),
                                     metricSuppliers.timers())
                             .time()) {
            decoded = decoder.decode(response, type);
        }

        if (body != null) {
            metricRegistry.histogram(
                    metricName.metricName(template.methodMetadata(), template.feignTarget(),
                            "response_size"),
                    metricSuppliers.histograms()).update(body.count());
        }

        return decoded;
    }
}
