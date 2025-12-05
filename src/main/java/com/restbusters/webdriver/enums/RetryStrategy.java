package com.restbusters.webdriver.enums;

import lombok.Getter;

/**
 * @author amatsaylo on 9/26/25
 * @project qreasp
 * Enumeration of retry strategies for failed actions.
 */

@Getter
public enum RetryStrategy {
    NO_RETRY(0, 0),
    IMMEDIATE_RETRY(3, 0),
    LINEAR_BACKOFF(3, 1000),
    EXPONENTIAL_BACKOFF(5, 500);

    private final int maxAttempts;
    private final long baseDelayMs;

    RetryStrategy(int maxAttempts, long baseDelayMs) {
        this.maxAttempts = maxAttempts;
        this.baseDelayMs = baseDelayMs;
    }
}
