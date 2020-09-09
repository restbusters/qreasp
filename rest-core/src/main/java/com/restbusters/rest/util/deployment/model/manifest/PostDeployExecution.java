package com.restbusters.rest.util.deployment.model.manifest;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author smatsaylo on 2019-12-10
 * @project dart
 */


public class PostDeployExecution extends ManifestModel {


    private Long id;


    private String state;


    private String status;


    private Long deploymentId;


    private String buildNumber;


    private String branchName;


    private String gitCommit;


    private Long pipeLineExecId;


    private String currentStageText;


    private String jobName;

    @JsonIgnore
    private PipelineExecution pipeLineExecution;


    private int percentageComplete;


    private String deploymentGuid;


    private String runIdGuid;


    private Long buildId;


    private String testGroup;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(Long deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getGitCommit() {
        return gitCommit;
    }

    public void setGitCommit(String gitCommit) {
        this.gitCommit = gitCommit;
    }

    public Long getPipeLineExecId() {
        return pipeLineExecId;
    }

    public void setPipeLineExecId(Long pipeLineExecId) {
        this.pipeLineExecId = pipeLineExecId;
    }

    public PipelineExecution getPipeLineExecution() {
        return pipeLineExecution;
    }

    public void setPipeLineExecution(PipelineExecution pipeLineExecution) {
        this.pipeLineExecution = pipeLineExecution;
    }

    public int getPercentageComplete() {
        return percentageComplete;
    }

    public void setPercentageComplete(int percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    public String getDeploymentGuid() {
        return deploymentGuid;
    }

    public void setDeploymentGuid(String deploymentGuid) {
        this.deploymentGuid = deploymentGuid;
    }

    public String getRunIdGuid() {
        return runIdGuid;
    }

    public void setRunIdGuid(String runIdGuid) {
        this.runIdGuid = runIdGuid;
    }

    public Long getBuildId() {
        return buildId;
    }

    public void setBuildId(Long buildId) {
        this.buildId = buildId;
    }

    public String getTestGroup() {
        return testGroup;
    }

    public void setTestGroup(String testGroup) {
        this.testGroup = testGroup;
    }

    public String getCurrentStageText() {
        return currentStageText;
    }

    public void setCurrentStageText(String currentStageText) {
        this.currentStageText = currentStageText;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
