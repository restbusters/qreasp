package com.restbusters.integration.testng.listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author restbusters on 10/15/18
 * @project qreasp
 */

public class RetryAnalyzer implements IRetryAnalyzer {

    private static int MAX_RETRY_COUNT = 1;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    AtomicInteger count = new AtomicInteger(MAX_RETRY_COUNT);

    public boolean isRetryAvailable() {
        return (count.intValue() > 0);
    }

    @Override
    public boolean retry(ITestResult result) {
        boolean retry = false;
        if (isRetryAvailable()) {
            logger.info("Retry requested for: " + result.getMethod() + ", " + (MAX_RETRY_COUNT - count.intValue() + 1) + " out of " + MAX_RETRY_COUNT);
            retry = true;
            count.decrementAndGet();
        }
        return retry;
    }


}
