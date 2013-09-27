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

package com.englishtown.vertx.metrics.integration;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.englishtown.vertx.metrics.VertxEventLoopGauges;
import org.junit.Test;
import org.vertx.testtools.TestVerticle;

import java.util.Map;
import java.util.SortedMap;

import static org.vertx.testtools.VertxAssert.*;

/**
 * Metrics integration tests
 */
public class BasicIntegrationTest extends TestVerticle {

    @Test
    public void testVertxEventLoopGauges() throws Exception {

        MetricRegistry registry = new MetricRegistry();
        VertxEventLoopGauges gauges = new VertxEventLoopGauges(vertx, container, registry);

        SortedMap<String, Gauge> results = registry.getGauges();

        assertTrue(results.size() > 0);
        for (Map.Entry<String, Gauge> entry : results.entrySet()) {
            assertEquals(0, entry.getValue().getValue());
        }

        testComplete();
    }

}
