/*
 * The MIT License (MIT)
 * Copyright © 2013 Englishtown <opensource@englishtown.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.englishtown.vertx.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Provides gauges for vert.x background pool worker threads
 */
public class VertxBackgroundPoolGauges {

    private static final Logger log = LoggerFactory.getLogger(VertxBackgroundPoolGauges.class);

    public VertxBackgroundPoolGauges(Vertx vertx, MetricRegistry registry) {
        register(vertx, registry);
    }

    protected void register(Vertx vertx,  MetricRegistry registry) {

        if (vertx instanceof VertxInternal) {
            VertxInternal vertxInternal = (VertxInternal) vertx;
            ExecutorService executorService = vertxInternal.getWorkerPool();

            if (executorService instanceof ThreadPoolExecutor) {
                final ThreadPoolExecutor executor = (ThreadPoolExecutor) executorService;
                try {
                    registry.register(
                            name(Utils.DEFAULT_METRIC_PREFIX, this.getClass().getSimpleName(), "queue", "size"),
                            new Gauge<Integer>() {
                                @Override
                                public Integer getValue() {
                                    return executor.getQueue().size();
                                }
                            });
                } catch (IllegalArgumentException e) {
                    // Assume this is due to multiple instances
                }
                try {
                    registry.register(
                            name(Utils.DEFAULT_METRIC_PREFIX, this.getClass().getSimpleName(), "size"),
                            new Gauge<Integer>() {
                                @Override
                                public Integer getValue() {
                                    return executor.getPoolSize();
                                }
                            });
                } catch (IllegalArgumentException e) {
                    // Assume this is due to multiple instances
                }
                try {
                    registry.register(
                            name(Utils.DEFAULT_METRIC_PREFIX, this.getClass().getSimpleName(), "core", "size"),
                            new Gauge<Integer>() {
                                @Override
                                public Integer getValue() {
                                    return executor.getCorePoolSize();
                                }
                            });
                } catch (IllegalArgumentException e) {
                    // Assume this is due to multiple instances
                }
                try {
                    registry.register(
                            name(Utils.DEFAULT_METRIC_PREFIX, this.getClass().getSimpleName(), "max", "size"),
                            new Gauge<Integer>() {
                                @Override
                                public Integer getValue() {
                                    return executor.getMaximumPoolSize();
                                }
                            });
                } catch (IllegalArgumentException e) {
                    // Assume this is due to multiple instances
                }
            }

        } else {
            log.warn("Vertx is not an instance of VertxInternal, cannot access worker background pool.");
        }

    }
}
