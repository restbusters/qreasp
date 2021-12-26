package com.restbusters.integraton.tc.client;

/**
 * @author Sasha Matsaylo
 * @project qreasp
 */
public class TcConstant {

    private TcConstant() { }

    public static final String JSON_PATH_BUILD_QUEUE_ID = "$.id";
    public static final String JSON_PATH_BUILD_STATE = "$.state";
    public static final String JSON_PATH_BUILD_STATUS = "$.status";
    public static final String JSON_PATH_BUILD_TYPE_ID = "$.buildTypeId";
    public static final String BUILD_STATE_RUNNING = "running";
    public static final String BUILD_STATE_FINISHED = "finished";
    public static final String BUILD_STATUS_SUCCESS = "SUCCESS";
    public static final String BUILD_STATUS_UNKNOWN = "UNKNOWN";
    public static final String BUILD_STATUS_FAILURE = "FAILURE";
    public static final String BUILD_STATE_QUEUED = "queued";
    public static final String ERROR_QUEUE_EXCEEDED_TIME = "Build has exceeded allowed time in the queue";
    public static final String ERROR_PRIORITY_BUILD_FAILURE = "Build with priority failed";
    public static final String ERROR_FAILED_TO_TRIGGER_BUILD = "Failed to trigger build";
}
