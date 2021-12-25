package com.restbusters.util.common;

public enum TaskStatus {

    STARTED("STARTED"),
    RUNNING("RUNNING"),
    FINISHED("FINISHED"),
    ABORTED("ABORTED");


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
