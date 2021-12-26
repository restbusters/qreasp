package com.restbusters.integraton.tc.client.model.task;

import com.restbusters.integraton.tc.client.model.post.job.PostBuild;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sasha Matsaylo on 12/25/21
 * @project qreasp
 */

@Data
public class BuildExecutorTask {

    private List<PostBuild> postBuild;
    private List<PostBuild> priorityBuilds;
    private boolean continueIfPriorityDeploymentFail = false;
    private boolean isPriorityDeploymentSuccess;
    private String taskName;
    private String taskStatus;
    private String description;
    private Map<String,BuildExecResult> buildMetaData;
    private int maxAttemptBuildCounter;
    private int maxWaitTime;
    private List<Map<String,String>> errors = new ArrayList<>();

}
