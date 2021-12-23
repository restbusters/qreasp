package com.restbusters.integraton.tc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Stopwatch;
import com.restbusters.integraton.tc.client.model.post.job.*;
import com.restbusters.integraton.tc.client.model.task.BuildExecutorTask;
import com.restbusters.resource.GlobalResourceManager;
import okhttp3.Response;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
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
 * @author Sasha Matsaylo on 12/25/21
 * @project qreasp
 */
public class TCBuildExecutor {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private TeamCityClient teamCityClient;
    private Map<String, String> buildMetaData;
    private BuildExecutorTask buildExecutorTask;

    public TCBuildExecutor(BuildExecutorTask buildExecutorTask, TeamCityClient teamCityClient) {
        this.buildExecutorTask = buildExecutorTask;
        this.teamCityClient = teamCityClient;
        this.buildMetaData = new HashMap<>();
    }

    private void executeBuild(PostBuild postBuild) {
        this.threadSleep(2000);
        String buildId = null;
        try {
            Response triggerJobResp = this.teamCityClient.postBuild(GlobalResourceManager.getInstance().getObjectMapper().writeValueAsString(postBuild));
            String triggerRespBody = triggerJobResp.body().string();
            if (!triggerJobResp.isSuccessful()) {
                buildMetaData.put(postBuild.getBuildType().getBuildTypeId(), triggerRespBody);
            } else {
                buildId = readBuildQueueId(triggerRespBody);
                ifBuildInQueueWait(buildId, this.buildExecutorTask.getMaxAttemptBuildCounter(),
                        this.buildExecutorTask.getMaxWaitTime());
                if (whatIsBuildState(buildId).equalsIgnoreCase(TcConstant.BUILD_STATE_QUEUED)) {
                    throw new IllegalStateException(TcConstant.EXCEPTION_MESSAGE_QUEUE_EXCEEDED_TIME);
                }
                setBuildMetaData(buildId);
                this.ifBuildRunningWait(buildId, this.buildExecutorTask.getMaxAttemptBuildCounter(),
                        this.buildExecutorTask.getMaxWaitTime());
                setBuildMetaData(buildId);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ifBuildInQueueWait(String buildQueueId, int maxAttempt, int waitTime) {
        String buildState = null;
        int buildQueueCounter = 0;
        boolean isBuildInQueue = true;
        while (isBuildInQueue && buildQueueCounter <= maxAttempt) {
            buildState = this.whatIsBuildState(buildQueueId);
            if (buildState.equalsIgnoreCase("queued")) {
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
            buildMetaData = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildMetaData;
    }

    private void setBuildMetaData(String buildId) {
        String buildMetaData = getBuildMetaData(buildId);
        if (StringUtils.isNotBlank(buildMetaData)) {
            this.buildMetaData.put(read(buildMetaData, TcConstant.JSON_PATH_BUILD_TYPE_ID), buildMetaData);
        }
    }

    private void ifBuildRunningWait(String buildId, int maxAttempt, int waitTime) {
        String buildState = null;
        int buildRunningCounter = 0;
        boolean isBuildRunning = true;
        while (isBuildRunning && buildRunningCounter <= maxAttempt) {
            buildState = this.whatIsBuildState(buildId);
            if (buildState.equalsIgnoreCase(TcConstant.BUILD_STATE_RUNNING)) {
                setBuildMetaData(buildId);
                this.threadSleep(waitTime);
            } else {
                isBuildRunning = false;
            }
            buildRunningCounter++;
        }
    }

    public Map<String, String> getBuildMetaData() {
        return buildMetaData;
    }

    public void executeBuilds() throws Exception {
        //RandomUtils.nextInt(1000, 5000);
        // create a pool of threads, 10 max jobs will execute in parallel
        ExecutorService threadPool = Executors.newFixedThreadPool(this.buildExecutorTask.getPostBuild().size());
        // submit jobs to be executing by the pool
        // clean resources
        List<Future> futures = new ArrayList<Future>();
        for (PostBuild postBuild : buildExecutorTask.getPostBuild()) {
            futures.add(threadPool.submit(new Callable<Void>() {
                public Void call() throws IOException, InterruptedException {
                    executeBuild(postBuild);
                    return null;
                }
            }));
            for (Future f : futures) {
                try {
                    f.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            // once you've submitted your last job to the service it should be shut down
            threadPool.shutdown();
            // wait for the threads to finish if necessary
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
    }
}
