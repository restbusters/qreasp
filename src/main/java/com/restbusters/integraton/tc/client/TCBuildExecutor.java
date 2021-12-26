package com.restbusters.integraton.tc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.restbusters.integraton.tc.client.model.post.job.PostBuild;
import com.restbusters.integraton.tc.client.model.task.BuildExecResult;
import com.restbusters.integraton.tc.client.model.task.BuildExecutorTask;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.util.common.GenericUtils;
import com.restbusters.util.common.TaskStatus;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        this.buildMetaData = new HashMap<>();
    }

    public BuildExecResult executeBuild(PostBuild postBuild) {
        logger.info("Starting to process {}", postBuild);
        BuildExecResult buildExecResult = new BuildExecResult();
        buildExecResult.setState(TaskStatus.STARTED.getValue());
        //this.threadSleep(GenericUtils.getRandomNumber(1000, 3000));
        String buildId = null;
        String triggerRespBody = null;
        try {
            Response triggerJobResp = this.teamCityClient.postBuild(GlobalResourceManager.getInstance().getObjectMapper().writeValueAsString(postBuild));
            if(triggerJobResp == null){
                setExecutionResults(buildExecResult, TaskStatus.ABORTED.getValue(),TcConstant.ERROR_FAILED_TO_TRIGGER_BUILD, postBuild );
            }
            else {
                triggerRespBody = triggerJobResp.body().string();
                if (!triggerJobResp.isSuccessful()) {
                    buildExecResult.setExecutionMetaData(triggerRespBody);
                    setExecutionResults(buildExecResult, TaskStatus.ABORTED.getValue(),TcConstant.ERROR_FAILED_TO_TRIGGER_BUILD, postBuild);
                } else {
                    setExecutionResults(buildExecResult, TaskStatus.RUNNING.getValue(), null, postBuild );
                    buildId = readBuildQueueId(triggerRespBody);
                    buildExecResult.setBuildId(buildId);
                    ifBuildInQueueWait(buildId, this.buildExecutorTask.getMaxAttemptBuildCounter(),
                            this.buildExecutorTask.getMaxWaitTime());
                    if (whatIsBuildState(buildId).equalsIgnoreCase(TcConstant.BUILD_STATE_QUEUED)) {
                        setExecutionResults(buildExecResult, TaskStatus.ABORTED.getValue(),TcConstant.ERROR_QUEUE_EXCEEDED_TIME, postBuild );
                        return buildExecResult;
                    }
                    setExecutionResults(buildExecResult, TaskStatus.RUNNING.getValue(),null, postBuild);
                    this.ifBuildRunningWait(buildExecResult, this.buildExecutorTask.getMaxAttemptBuildCounter(),
                            this.buildExecutorTask.getMaxWaitTime());
                    setExecutionResults(buildExecResult, TaskStatus.FINISHED.getValue(),null, postBuild);
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

    private void setExecutionResults(BuildExecResult buildExecResult, String taskState, @Nullable  String taskError, PostBuild postBuild){
        buildExecResult.setState(taskState);
        buildExecResult.setErrors(taskError);
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
                //this.threadSleep(waitTime);
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
                //this.threadSleep(waitTime);
            } else {
                isBuildRunning = false;
            }
            buildRunningCounter++;
        }
    }

    public void executeBuilds() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(this.buildExecutorTask.getPostBuild().size());
        List<Future> futures = new ArrayList<Future>();
        for (PostBuild postBuild : this.buildExecutorTask.getPostBuild()) {
            futures.add(executorService.submit(new Callable<Void>() {
                public Void call() throws IOException, InterruptedException {
                    executeBuild(postBuild);
                    return null;
                }
            }));
            boolean areAllBuildsCompleted = false;
            while (areAllBuildsCompleted){
                logger.info("waiting");
                for (Future f : futures) {
                    if(f.isDone()){
                        areAllBuildsCompleted = true;
                    }
                    else {
                        areAllBuildsCompleted = false;
                    }
                }
            }
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    public BuildExecutorTask getBuildExecutorTask() {
        return buildExecutorTask;
    }
}
