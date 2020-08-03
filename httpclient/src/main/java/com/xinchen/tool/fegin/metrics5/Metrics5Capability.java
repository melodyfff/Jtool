package com.xinchen.tool.fegin.metrics5;

import feign.Capability;
import feign.Client;
import feign.Feign;
import feign.FeignException;
import feign.InvocationHandlerFactory;
import feign.Target;
import feign.Util;
import feign.codec.Decoder;
import feign.codec.Encoder;
import io.dropwizard.metrics5.MetricRegistry;
import io.dropwizard.metrics5.SharedMetricRegistries;
import io.dropwizard.metrics5.Timer.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/8/4 0:02
 */
public class Metrics5Capability implements Capability {

    private final MetricRegistry metricRegistry;
    private final MetricSuppliers metricSuppliers;

    public Metrics5Capability() {
        this(SharedMetricRegistries.getOrCreate("feign"), new MetricSuppliers());
    }

    public Metrics5Capability(MetricRegistry metricRegistry) {
        this(metricRegistry, new MetricSuppliers());
    }

    public Metrics5Capability(MetricRegistry metricRegistry, MetricSuppliers metricSuppliers) {
        this.metricRegistry = metricRegistry;
        this.metricSuppliers = metricSuppliers;
    }

    @Override
    public Client enrich(Client client) {
        return new MeteredClient(client, metricRegistry, metricSuppliers);
    }

    @Override
    public Encoder enrich(Encoder encoder) {
        return new MeteredEncoder(encoder, metricRegistry, metricSuppliers);
    }

    @Override
    public Decoder enrich(Decoder decoder) {
        return new MeteredDecoder(decoder, metricRegistry, metricSuppliers);
    }

    @Override
    public InvocationHandlerFactory enrich(InvocationHandlerFactory invocationHandlerFactory) {
        return new MeteredInvocationHandleFactory(invocationHandlerFactory, metricRegistry, metricSuppliers);
    }


    /**
     * Warp feign {@link InvocationHandler} with metrics.
     */
    static class MeteredInvocationHandleFactory implements InvocationHandlerFactory {
        private static final Logger LOG = LoggerFactory.getLogger(MeteredInvocationHandleFactory.class);
        /**
         * Methods that are declared by super class object and, if invoked, we don't wanna record metrics
         * for
         */
        private static final List<String> JAVA_OBJECT_METHODS =
                Arrays.asList("equals", "toString", "hashCode");
        private final InvocationHandlerFactory invocationHandler;

        private final MetricRegistry metricRegistry;

        private final FeignMetricName metricName;

        private final MetricSuppliers metricSuppliers;

        public MeteredInvocationHandleFactory(InvocationHandlerFactory invocationHandler,
                                              MetricRegistry metricRegistry, MetricSuppliers metricSuppliers) {
            this.invocationHandler = invocationHandler;
            this.metricRegistry = metricRegistry;
            this.metricSuppliers = metricSuppliers;
            this.metricName = new FeignMetricName(Feign.class);
        }

        @Override
        public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
            final Class clientClass = target.type();

            final InvocationHandler invocationHandle = invocationHandler.create(target, dispatch);
            return (proxy, method, args) -> {

                if (JAVA_OBJECT_METHODS.contains(method.getName())
                        || Util.isDefault(method)) {
                    LOG.trace("Skipping metrics for method={}", method);
                    return invocationHandle.invoke(proxy, method, args);
                }

                try (final Context classTimer =
                             metricRegistry.timer(metricName.metricName(clientClass, method, target.url()),
                                     metricSuppliers.timers()).time()) {

                    return invocationHandle.invoke(proxy, method, args);
                } catch (final FeignException e) {
                    metricRegistry.meter(
                            metricName.metricName(clientClass, method, target.url())
                                    .resolve("http_error")
                                    .tagged("http_status", String.valueOf(e.status()))
                                    .tagged("error_group", e.status() / 100 + "xx"),
                            metricSuppliers.meters()).mark();

                    throw e;
                } catch (final Throwable e) {
                    metricRegistry
                            .meter(metricName.metricName(clientClass, method, target.url())
                                            .resolve("exception")
                                            .tagged("exception_name", e.getClass().getSimpleName()),
                                    metricSuppliers.meters())
                            .mark();

                    throw e;
                }
            };
        }
    }

}
