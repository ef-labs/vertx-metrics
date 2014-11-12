package com.englishtown.vertx.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Metric utilities to set up defaults for a Verticle
 */
public class Utils {

    private static Logger log = LoggerFactory.getLogger(Utils.class);

    public static final String DEFAULT_METRIC_PREFIX = "et.metrics";

    // A JVM unique ID for when multiple verticle instances are running
    private static final AtomicInteger REPORTER_ID = new AtomicInteger(0);


    public static JmxReporter create(Verticle verticle, MetricRegistry registry, JsonObject config) {
        return create(verticle, registry, null, config);
    }

    public static JmxReporter create(Verticle verticle, MetricRegistry registry, boolean jmxReporter) {
        return create(verticle, registry, null, jmxReporter);
    }

    public static JmxReporter create(Verticle verticle, MetricRegistry registry, Map<String, Object> values, JsonObject config) {
        return create(verticle, registry, values, config.getBoolean("jmx-reporter", false), config.getString("jmx-reporter-domain"));
    }

    public static JmxReporter create(Verticle verticle, MetricRegistry registry, Map<String, Object> values, boolean jmxReporter) {
        return create(verticle, registry, values, jmxReporter, null);
    }

    public static JmxReporter create(Verticle verticle, MetricRegistry registry, Map<String, Object> values, boolean jmxReporter, String domain) {

        try {
            new VerticleGauges(verticle, registry, values);
        } catch (Throwable t) {
            log.warn("Error creating VerticleGauges", t);
        }
        try {
            new VertxEventLoopGauges(verticle.getVertx(), registry);
        } catch (Throwable t) {
            log.warn("Error creating VertxEventLoopGauges", t);
        }
        try {
            new VertxBackgroundPoolGauges(verticle.getVertx(), registry);
        } catch (Throwable t) {
            log.warn("Error creating VertxBackgroundPoolGauges", t);
        }

        if (domain == null || domain.isEmpty()) {
            domain = "et.metrics";
        }

        // Guarantee unique name
        domain += "-" + REPORTER_ID.incrementAndGet();

        if (jmxReporter) {
            JmxReporter reporter = JmxReporter
                    .forRegistry(registry)
                    .inDomain(domain)
                    .build();

            reporter.start();
            return reporter;
        }

        return null;

    }

}
