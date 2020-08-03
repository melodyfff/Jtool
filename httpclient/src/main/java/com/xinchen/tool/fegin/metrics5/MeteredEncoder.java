package com.xinchen.tool.fegin.metrics5;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import io.dropwizard.metrics5.MetricRegistry;
import io.dropwizard.metrics5.Timer.Context;

import java.lang.reflect.Type;

/**
 * Warp feign {@link Encoder} with metrics.
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/8/3 23:58
 */
public class MeteredEncoder implements Encoder {

    private final Encoder encoder;
    private final MetricRegistry metricRegistry;
    private final MetricSuppliers metricSuppliers;
    private final FeignMetricName metricName;

    public MeteredEncoder(Encoder encoder, MetricRegistry metricRegistry,
                          MetricSuppliers metricSuppliers) {
        this.encoder = encoder;
        this.metricRegistry = metricRegistry;
        this.metricSuppliers = metricSuppliers;
        this.metricName = new FeignMetricName(Encoder.class);
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template)
            throws EncodeException {
        try (final Context classTimer =
                     metricRegistry.timer(
                             metricName.metricName(template.methodMetadata(), template.feignTarget()),
                             metricSuppliers.timers()).time()) {
            encoder.encode(object, bodyType, template);
        }

        if (template.body() != null) {
            metricRegistry.histogram(
                    metricName.metricName(template.methodMetadata(), template.feignTarget(), "request_size"),
                    metricSuppliers.histograms()).update(template.body().length);
        }
    }

}
