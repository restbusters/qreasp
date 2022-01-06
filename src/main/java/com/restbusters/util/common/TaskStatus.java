package com.restbusters.util.common;

public enum TaskStatus {

    SUCCESS("SUCCESS"),
    FAILURE("FAILURE");

    String value;

    TaskStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

}
