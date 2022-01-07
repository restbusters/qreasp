package com.restbusters.integraton.tc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;
import com.restbusters.integraton.tc.client.model.post.job.PostBuild;
import com.restbusters.integraton.tc.client.model.task.BuildExecResult;
import com.restbusters.integraton.tc.client.model.task.BuildExecutorTask;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.util.common.GenericUtils;
import com.restbusters.util.common.TaskState;
import com.restbusters.util.common.TaskStatus;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

import static com.jayway.jsonpath.JsonPath.read;

/**
 * @author Sasha Matsaylo
 * @project qreasp
 */
public class TCBuildExecutor {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private TeamCityClient teamCityClient;
    private Map<String, BuildExecResult> buildMetaData;
    private BuildExecutorTask buildExecutorTask;

    public TCBuildExecutor(BuildExecutorTask buildExecutorTask, TeamCityClient teamCityClient) {
        this.buildExecutorTask = buildExecutorTask;
        this.teamCityClient = teamCityClient;
        this.buildMetaData = new LinkedHashMap();
    }

    public void initBuildExecutorTask(BuildExecutorTask buildExecutorTask) {
        this.buildExecutorTask = buildExecutorTask;
    }

    private BuildExecResult executeBuild(PostBuild postBuild) {
        logger.info("Starting to process {}", postBuild);
        BuildExecResult buildExecResult = new BuildExecResult();
        buildExecResult.setState(TaskState.STARTED.getValue());
        this.threadSleep(GenericUtils.getRandomNumber(1000, 3000));
        String buildId = null;
        String triggerRespBody = null;
        try {
            Response triggerJobResp = this.teamCityClient.postBuild(GlobalResourceManager.getInstance().getObjectMapper().writeValueAsString(postBuild));
            if (triggerJobResp == null) {
                setExecutionResults(buildExecResult, TaskState.ABORTED.getValue(), TcConstant.ERROR_FAILED_TO_TRIGGER_BUILD, postBuild);
            } else {
                triggerRespBody = triggerJobResp.body().string();
                if (!triggerJobResp.isSuccessful()) {
                    buildExecResult.setExecutionMetaData(triggerRespBody);
                    setExecutionResults(buildExecResult, TaskState.ABORTED.getValue(), TcConstant.ERROR_FAILED_TO_TRIGGER_BUILD, postBuild);
                } else {
                    setExecutionResults(buildExecResult, TaskState.RUNNING.getValue(), null, postBuild);
                    buildId = readBuildQueueId(triggerRespBody);
                    buildExecResult.setBuildId(buildId);
                    ifBuildInQueueWait(buildId, this.buildExecutorTask.getMaxAttemptBuildCounter(),
                            this.buildExecutorTask.getMaxWaitTime());
                    if (whatIsBuildState(buildId).equalsIgnoreCase(TcConstant.BUILD_STATE_QUEUED)) {
                        setExecutionResults(buildExecResult, TaskState.ABORTED.getValue(), TcConstant.ERROR_QUEUE_EXCEEDED_TIME, postBuild);
                        return buildExecResult;
                    }
                    setExecutionResults(buildExecResult, TaskState.RUNNING.getValue(), null, postBuild);
                    this.ifBuildRunningWait(buildExecResult, this.buildExecutorTask.getMaxAttemptBuildCounter(),
                            this.buildExecutorTask.getMaxWaitTime());
                    setExecutionResults(buildExecResult, TaskState.FINISHED.getValue(), null, postBuild);
                }
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildExecResult;
    }

    private void setExecutionResults(BuildExecResult buildExecResult, String taskState, @Nullable String taskError, PostBuild postBuild) {
        buildExecResult.setState(taskState);
        buildExecResult.getErrors().add(taskError);
        this.buildMetaData.put(postBuild.getBuildType().getBuildTypeId(), buildExecResult);
        this.buildExecutorTask.setBuildMetaData(this.buildMetaData);
    }

    private void ifBuildInQueueWait(String buildQueueId, int maxAttempt, int waitTime) {
        String buildState = null;
        int buildQueueCounter = 0;
        boolean isBuildInQueue = true;
        while (isBuildInQueue && buildQueueCounter <= maxAttempt) {
            buildState = this.whatIsBuildState(buildQueueId);
            if (buildState.equalsIgnoreCase(TcConstant.BUILD_STATE_QUEUED)) {
                this.threadSleep(waitTime);
            } else {
                isBuildInQueue = false;
            }
            buildQueueCounter++;
        }
    }

    private void threadSleep(int waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String readBuildQueueId(String metaData) {
        return Integer.toString(read(metaData, TcConstant.JSON_PATH_BUILD_QUEUE_ID));
    }

    private String whatIsBuildState(String buildQueueId) {
        String responseBody = this.getBuildMetaData(buildQueueId);
        return read(responseBody, TcConstant.JSON_PATH_BUILD_STATE);
    }

    private String getBuildMetaData(String buildId) {
        String buildMetaData = null;
        try {
            Response response = this.teamCityClient.getBuildById(buildId);
            if (response != null) {
                buildMetaData = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildMetaData;
    }

    private String setBuildMetaData(BuildExecResult buildExecResult) {
        String buildMetaData = getBuildMetaData(buildExecResult.getBuildId());
        if (StringUtils.isNotBlank(buildMetaData)) {
            buildExecResult.setExecutionMetaData(buildMetaData);
        }
        return buildMetaData;
    }

    private void ifBuildRunningWait(BuildExecResult buildExecResult, int maxAttempt, int waitTime) {
        String buildState = null;
        int buildRunningCounter = 0;
        boolean isBuildRunning = true;
        while (isBuildRunning && buildRunningCounter <= maxAttempt) {
            buildState = this.whatIsBuildState(buildExecResult.getBuildId());
            if (buildState.equalsIgnoreCase(TcConstant.BUILD_STATE_RUNNING)) {
                setBuildMetaData(buildExecResult);
                this.threadSleep(waitTime);
            } else {
                isBuildRunning = false;
                setBuildMetaData(buildExecResult);
            }
            buildRunningCounter++;
        }
    }

    private BuildExecutorTask executeBuildsParallel() {
        ForkJoinPool pool = forkJoinPoolGet(this.buildExecutorTask.getPostBuild().size(), true);
        buildExecutorTask.getPostBuild()
                .parallelStream()
                .forEach(
                        postBuild -> {
                            try {
                                pool.submit(() -> executeBuild(postBuild)).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } finally {
                                if (pool != null) {
                                    pool.shutdown(); //always remember to shutdown the pool
                                }
                            }
                        });

        return finalizeResult(this.buildExecutorTask);
    }

    private BuildExecutorTask executeBuildsSequential() {
        for (PostBuild postBuild : buildExecutorTask.getPostBuild()) {
            BuildExecResult buildExecResult = this.executeBuild(postBuild);
            if (buildExecResult.getState().equalsIgnoreCase(TaskState.FINISHED.getValue())) {
                String status = JsonPath.read(buildExecResult.getExecutionMetaData(), TcConstant.JSON_PATH_BUILD_STATUS);
                if (status.equalsIgnoreCase(TcConstant.BUILD_STATUS_FAILURE) || status.equalsIgnoreCase(TcConstant.BUILD_STATUS_UNKNOWN)) {
                    setBuildErrors(postBuild.getBuildType().getBuildTypeId(), TcConstant.ERROR_PRIORITY_BUILD_FAILURE);
                    if (!this.buildExecutorTask.isContinueIfSequentialDeploymentFail()) {
                        break;
                    }
                }
            }
            else if(buildExecResult.getState().equalsIgnoreCase(TaskState.ABORTED.getValue())){
                if (!this.buildExecutorTask.isContinueIfSequentialDeploymentFail()) {
                    break;
                }
            }
        }
        return finalizeResult(this.buildExecutorTask);
    }


    private BuildExecutorTask finalizeResult(BuildExecutorTask buildExecutorTask) {
        buildExecutorTask.setTaskStatus(TaskStatus.SUCCESS.getValue());
        for (Map.Entry<String, BuildExecResult> entry : buildExecutorTask.getBuildMetaData().entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue().getExecutionMetaData())) {
                String buildStatus = null;
                if (entry.getValue().getState().equalsIgnoreCase(TcConstant.BUILD_STATE_FINISHED)) {
                    try {
                        buildStatus = JsonPath.read(entry.getValue().getExecutionMetaData(), TcConstant.JSON_PATH_BUILD_STATUS);
                        if (buildStatus == null && !buildStatus.equalsIgnoreCase(TcConstant.BUILD_STATUS_SUCCESS)) {
                            buildExecutorTask.setTaskStatus(TaskStatus.FAILURE.getValue());
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        buildExecutorTask.setTaskStatus(TaskStatus.FAILURE.getValue());
                    }
                } else {
                    buildExecutorTask.setTaskStatus(TaskStatus.FAILURE.getValue());
                }
            }
        }
        buildExecutorTask.setTaskState(TaskState.FINISHED.getValue());
        return buildExecutorTask;
    }

    public Future<BuildExecutorTask> executeBuildsAsync() throws InterruptedException {
        CompletableFuture<BuildExecutorTask> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            Thread.sleep(500);
            if(this.buildExecutorTask.isDeploymentSequential()){
                completableFuture.complete(this.executeBuildsSequential());
            }
            else {
                completableFuture.complete(this.executeBuildsParallel());
            }
            return null;
        });

        return completableFuture;
    }

    private ForkJoinPool forkJoinPoolGet(int threadCount, boolean asyncMode) {
        return new ForkJoinPool(
                threadCount, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, asyncMode);
    }

    public BuildExecutorTask getBuildExecutorTask() {
        return buildExecutorTask;
    }

    private void setBuildErrors(String errorKey, String errorValue) {
        Map<String, String> result = new HashMap<>();
        result.put(errorKey, errorValue);
        this.buildExecutorTask.getErrors().add(result);
    }
}
