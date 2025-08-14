package com.restbusters.http.helper;

public enum ExecutionState {
    STARTED,
    COMPLETED,
    SKIPPED,
    UNKNOWN;

    public static ExecutionState fromString(String status) {
        try {
            return ExecutionState.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
