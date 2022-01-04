package com.restbusters.util.common;

public enum TaskState {

    STARTED("STARTED"),
    RUNNING("RUNNING"),
    FINISHED("FINISHED"),
    ABORTED("ABORTED");


    String value;

    TaskState(final String value) {
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
