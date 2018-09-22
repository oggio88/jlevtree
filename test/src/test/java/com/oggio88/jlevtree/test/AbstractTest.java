package com.oggio88.jlevtree.test;

import org.junit.Rule;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by walter on 15/06/17.
 */
public abstract class AbstractTest {
    protected abstract Logger getLogger();

    private void logInfo(Description description, String status, long nanos) {
        String testName = description.getMethodName();
        getLogger().info(String.format("Test %s %s, spent %d ms",
                testName, status, TimeUnit.NANOSECONDS.toMillis(nanos)));
    }

    @Rule
    public Stopwatch stopwatch = new Stopwatch() {
        @Override
        protected void finished(long nanos, Description description) {
            logInfo(description, "finished", nanos);
        }
    };
}
