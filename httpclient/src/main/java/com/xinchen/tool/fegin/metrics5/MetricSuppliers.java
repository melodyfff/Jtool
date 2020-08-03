package com.xinchen.tool.fegin.metrics5;


import io.dropwizard.metrics5.Histogram;
import io.dropwizard.metrics5.Meter;
import io.dropwizard.metrics5.MetricRegistry.MetricSupplier;
import io.dropwizard.metrics5.SlidingTimeWindowArrayReservoir;
import io.dropwizard.metrics5.Timer;

import java.util.concurrent.TimeUnit;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/8/3 23:49
 */
public class MetricSuppliers {
    public MetricSupplier<Timer> timers() {
        // only keep timer data for 1 minute
        return () -> new Timer(new SlidingTimeWindowArrayReservoir(1, TimeUnit.MINUTES));
    }

    public MetricSupplier<Meter> meters() {
        return Meter::new;
    }

    public MetricSupplier<Histogram> histograms() {
        // only keep timer data for 1 minute
        return () -> new Histogram(new SlidingTimeWindowArrayReservoir(1, TimeUnit.MINUTES));
    }
}
