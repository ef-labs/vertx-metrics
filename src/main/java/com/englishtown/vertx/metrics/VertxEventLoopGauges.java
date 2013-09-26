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
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.impl.VertxInternal;
import org.vertx.java.platform.Container;

import javax.inject.Inject;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Provides gauges for vertx event loops
 */
public class VertxEventLoopGauges {

    private final Vertx vertx;
    private final Container container;

    @Inject
    public VertxEventLoopGauges(Vertx vertx, Container container) {
        this.vertx = vertx;
        this.container = container;
    }

    public void register(MetricRegistry metricRegistry) {

        if (vertx instanceof VertxInternal) {
            VertxInternal vertxInternal = (VertxInternal) vertx;
            int count = 0;

            for (EventExecutor ee : vertxInternal.getEventLoopGroup()) {
                if (ee instanceof SingleThreadEventExecutor) {
                    final SingleThreadEventExecutor executor = (SingleThreadEventExecutor) ee;
                    try {
                        metricRegistry.register(
                                name(VertxEventLoopGauges.class, "executor-" + count++, "pendingTasks"),
                                new Gauge<Integer>() {
                                    @Override
                                    public Integer getValue() {
                                        return executor.pendingTasks();
                                    }
                                });
                    } catch (IllegalArgumentException e) {
                        container.logger().warn(e.getMessage(), e);
                    }
                }
            }

        } else {
            container.logger().warn("Vertx is not an instance of VertxInternal, cannot access event loops.");
        }

    }

}
