package com.restbusters.integraton.tc.client;

/**
 * @author Sasha Matsaylo on 12/25/21
 * @project qreasp
 */
public class TcConstant {

    private TcConstant() { }

    public static final String JSON_PATH_BUILD_QUEUE_ID = "$.id";
    public static final String JSON_PATH_BUILD_STATE = "$.state";
    public static final String JSON_PATH_BUILD_TYPE_ID = "$.buildTypeId";
    public static final String BUILD_STATE_RUNNING = "running";
    public static final String BUILD_STATE_FINISHED = "finished";
    public static final String BUILD_STATE_QUEUED = "queued";
    public static final String EXCEPTION_MESSAGE_QUEUE_EXCEEDED_TIME = "Build has exceeded allowed time in the queue";
}
