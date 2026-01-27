package com.restbusters.webdriver.enums;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests for RetryStrategy enum.
 */
public class RetryStrategyTest {

    @Test(description = "Test all RetryStrategy values exist")
    public void testAllRetryStrategiesExist() {
        assertEquals(RetryStrategy.values().length, 4, "Should have 4 retry strategies");

        assertNotNull(RetryStrategy.NO_RETRY);
        assertNotNull(RetryStrategy.IMMEDIATE_RETRY);
        assertNotNull(RetryStrategy.LINEAR_BACKOFF);
        assertNotNull(RetryStrategy.EXPONENTIAL_BACKOFF);
    }

    @Test(description = "Test NO_RETRY configuration")
    public void testNoRetryConfiguration() {
        RetryStrategy strategy = RetryStrategy.NO_RETRY;

        assertEquals(strategy.getMaxAttempts(), 0);
        assertEquals(strategy.getBaseDelayMs(), 0);
    }

    @Test(description = "Test IMMEDIATE_RETRY configuration")
    public void testImmediateRetryConfiguration() {
        RetryStrategy strategy = RetryStrategy.IMMEDIATE_RETRY;

        assertEquals(strategy.getMaxAttempts(), 3);
        assertEquals(strategy.getBaseDelayMs(), 0);
    }

    @Test(description = "Test LINEAR_BACKOFF configuration")
    public void testLinearBackoffConfiguration() {
        RetryStrategy strategy = RetryStrategy.LINEAR_BACKOFF;

        assertEquals(strategy.getMaxAttempts(), 3);
        assertEquals(strategy.getBaseDelayMs(), 1000);
    }

    @Test(description = "Test EXPONENTIAL_BACKOFF configuration")
    public void testExponentialBackoffConfiguration() {
        RetryStrategy strategy = RetryStrategy.EXPONENTIAL_BACKOFF;

        assertEquals(strategy.getMaxAttempts(), 5);
        assertEquals(strategy.getBaseDelayMs(), 500);
    }

    @Test(description = "Test valueOf")
    public void testValueOf() {
        assertEquals(RetryStrategy.valueOf("NO_RETRY"), RetryStrategy.NO_RETRY);
        assertEquals(RetryStrategy.valueOf("IMMEDIATE_RETRY"), RetryStrategy.IMMEDIATE_RETRY);
        assertEquals(RetryStrategy.valueOf("LINEAR_BACKOFF"), RetryStrategy.LINEAR_BACKOFF);
        assertEquals(RetryStrategy.valueOf("EXPONENTIAL_BACKOFF"), RetryStrategy.EXPONENTIAL_BACKOFF);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          description = "Test valueOf with invalid name throws exception")
    public void testValueOfInvalidThrowsException() {
        RetryStrategy.valueOf("INVALID_STRATEGY");
    }

    @Test(description = "Test ordinal values")
    public void testOrdinalValues() {
        assertEquals(RetryStrategy.NO_RETRY.ordinal(), 0);
        assertEquals(RetryStrategy.IMMEDIATE_RETRY.ordinal(), 1);
        assertEquals(RetryStrategy.LINEAR_BACKOFF.ordinal(), 2);
        assertEquals(RetryStrategy.EXPONENTIAL_BACKOFF.ordinal(), 3);
    }

    @Test(description = "Test strategy selection logic")
    public void testStrategySelectionLogic() {
        // Simulate choosing strategy based on action criticality
        RetryStrategy fastAction = RetryStrategy.NO_RETRY;
        RetryStrategy normalAction = RetryStrategy.IMMEDIATE_RETRY;
        RetryStrategy networkAction = RetryStrategy.LINEAR_BACKOFF;
        RetryStrategy criticalAction = RetryStrategy.EXPONENTIAL_BACKOFF;

        assertTrue(fastAction.getMaxAttempts() < normalAction.getMaxAttempts());
        assertTrue(normalAction.getBaseDelayMs() < networkAction.getBaseDelayMs());
        assertTrue(criticalAction.getMaxAttempts() >= networkAction.getMaxAttempts());
    }
}
