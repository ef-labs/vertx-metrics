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
            register(verticle, registry);
            registerValues(verticle, registry, values);
        } catch (IllegalArgumentException e) {
            // Assume this is due to multiple instances running
        }
    }

    protected void register(Verticle verticle, MetricRegistry registry) {

        // Catch NoSuchMethodError for backwards compatibility
        String config;
        try {
            config = verticle.getContainer().config().encodePrettily();
        } catch (NoSuchMethodError e) {
            config = verticle.getContainer().config().encode();
        }

        final String json = config;
        registry.register(name(verticle.getClass(), "config"), new Gauge<String>() {
            @Override
            public String getValue() {
                return json;
            }
        });

        final Boolean eventLoop = verticle.getVertx().isEventLoop();
        registry.register(name(verticle.getClass(), "event-loop"), new Gauge<Boolean>() {
            @Override
            public Boolean getValue() {
                return eventLoop;
            }
        });

        final Boolean worker = verticle.getVertx().isWorker();
        registry.register(name(verticle.getClass(), "worker"), new Gauge<Boolean>() {
            @Override
            public Boolean getValue() {
                return worker;
            }
        });

    }

    protected void registerValues(Verticle verticle, MetricRegistry registry, Map<String, Object> values) {

        if (values == null) {
            return;
        }

        for (final Map.Entry<String, Object> entry : values.entrySet()) {
            registry.register(name(verticle.getClass(), entry.getKey()), new Gauge<Object>() {
                @Override
                public Object getValue() {
                    return entry.getValue();
                }
            });
        }

    }

}
