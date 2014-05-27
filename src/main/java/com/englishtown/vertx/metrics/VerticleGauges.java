package com.englishtown.vertx.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.vertx.java.platform.Verticle;

import java.util.Map;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Registers gauges for a verticle
 */
public class VerticleGauges {

    /**
     * Registers gauges for verticle config, event-loop, worker
     *
     * @param verticle the verticle to register gauges for
     * @param registry the registry to add the gauges to
     */
    public VerticleGauges(Verticle verticle, MetricRegistry registry) {
        this(verticle, registry, null);
    }

    /**
     * Registers gauges for verticle config, event-loop, worker
     *
     * @param verticle the verticle to register gauges for
     * @param registry the registry to add the gauges to
     * @param values   optional additional values to register gauges for
     */
    public VerticleGauges(Verticle verticle, MetricRegistry registry, Map<String, Object> values) {
        try {
            String prefix = getMetricNamePrefix(verticle);
            register(verticle, registry, prefix);
            registerValues(verticle, registry, values, prefix);
        } catch (IllegalArgumentException e) {
            // Assume this is due to multiple instances running
        }
    }

    protected String getMetricNamePrefix(Verticle verticle) {

        StringBuilder sb = new StringBuilder();
        String[] parts = verticle.getClass().getName().split("\\.");

        for (int i = 0; i < parts.length; i++) {
            if (i == parts.length - 1) {
                sb.append(parts[i]);
            } else {
                sb.append(parts[i].substring(0, 1)).append(".");
            }
        }

        return sb.toString();
    }

    protected void register(Verticle verticle, MetricRegistry registry, String prefix) {

        // Catch NoSuchMethodError for backwards compatibility
        String config;
        try {
            config = verticle.getContainer().config().encodePrettily();
        } catch (NoSuchMethodError e) {
            config = verticle.getContainer().config().encode();
        }

        final String json = config;
        registry.register(name(prefix, "config"), new Gauge<String>() {
            @Override
            public String getValue() {
                return json;
            }
        });

        final Boolean eventLoop = verticle.getVertx().isEventLoop();
        registry.register(name(prefix, "event-loop"), new Gauge<Boolean>() {
            @Override
            public Boolean getValue() {
                return eventLoop;
            }
        });

        final Boolean worker = verticle.getVertx().isWorker();
        registry.register(name(prefix, "worker"), new Gauge<Boolean>() {
            @Override
            public Boolean getValue() {
                return worker;
            }
        });

    }

    protected void registerValues(Verticle verticle, MetricRegistry registry, Map<String, Object> values, String prefix) {

        if (values == null) {
            return;
        }

        for (final Map.Entry<String, Object> entry : values.entrySet()) {
            registry.register(name(prefix, entry.getKey()), new Gauge<Object>() {
                @Override
                public Object getValue() {
                    return entry.getValue();
                }
            });
        }

    }

}
