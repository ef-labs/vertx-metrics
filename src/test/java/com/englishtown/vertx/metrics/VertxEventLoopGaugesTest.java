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

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.impl.VertxInternal;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link VertxEventLoopGauges}
 */
@RunWith(MockitoJUnitRunner.class)
public class VertxEventLoopGaugesTest {

    VertxEventLoopGauges vertxEventLoopGauges;
    NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);

    @Mock
    VertxInternal vertx;
    @Mock
    Container container;
    @Mock
    Logger logger;
    @Mock
    MetricRegistry registry;

    @Before
    public void setUp() {
        when(vertx.getEventLoopGroup()).thenReturn(eventLoopGroup);
        when(container.logger()).thenReturn(logger);
        vertxEventLoopGauges = new VertxEventLoopGauges(vertx, container);
    }

    @Test
    public void testRegister() throws Exception {

        vertxEventLoopGauges.register(registry);
        verify(registry, times(2)).register(anyString(), any(Metric.class));

    }

    @Test
    public void testRegister_Not_VertxInternal() throws Exception {

        Vertx vertx = mock(Vertx.class);
        vertxEventLoopGauges = new VertxEventLoopGauges(vertx, container);
        vertxEventLoopGauges.register(registry);

        verify(registry, never()).register(anyString(), any(Metric.class));
        verify(logger).warn(any());

    }

    @Test
    public void testRegister_Already_Registered() throws Exception {

        when(registry.register(anyString(), any(Metric.class))).thenThrow(new IllegalArgumentException());

        vertxEventLoopGauges.register(registry);
        verify(logger, times(2)).warn(any(), any(Throwable.class));

    }

}
