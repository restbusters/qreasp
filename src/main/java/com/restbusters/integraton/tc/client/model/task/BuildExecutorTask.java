package com.restbusters.integraton.tc.client.model.task;

import com.restbusters.integraton.tc.client.model.post.job.PostBuild;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * @author Sasha Matsaylo on 12/25/21
 * @project qreasp
 */

@Data
public class BuildExecutorTask {

    private List<PostBuild> postBuild;
    private String taskName;
    private String taskStatus;
    private String description;
    private Map<String,String> buildMetaData;
    private int maxAttemptBuildCounter;
    private int maxWaitTime;

}
