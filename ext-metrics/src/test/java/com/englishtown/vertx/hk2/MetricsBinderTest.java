package com.englishtown.vertx.hk2;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 9/26/13
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetricsBinderTest {

    MetricsBinder binder = new MetricsBinder();

    @Test
    public void testConfigure() throws Exception {
        binder.configure();
    }
}
