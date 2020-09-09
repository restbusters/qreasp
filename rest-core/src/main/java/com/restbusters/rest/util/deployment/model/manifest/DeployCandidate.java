package com.restbusters.rest.util.deployment.model.manifest;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * @author smatsaylo on 2019-11-18
 * @project dart
 */

public class DeployCandidate extends ManifestModel {

    private Long id;
    private String state;
    private String status;
    private Long deploymentId;
    private Long serviceId;
    private String buildNumber;
    private String branchName;
    private String gitCommit;
    private String buildTypeId;

    @JsonIgnore
    private ManifestDeployment manifestDeployment;
    private ServiceItem serviceItem;
    private List<DeployCandidateChanges> deployCandidateChanges;


    private int percentageComplete;


    private String deploymentGuid;


    private String healthStatus;


    private String changesHref;


    private String webUrl;


    private String lastChangeId;


    private String statusText;


    private String currentStageText;

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

    public Long getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(Long deploymentId) {
        this.deploymentId = deploymentId;
    }

    public ManifestDeployment getManifestDeployment() {
        return manifestDeployment;
    }

    public void setManifestDeployment(ManifestDeployment manifestDeployment) {
        this.manifestDeployment = manifestDeployment;
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

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public int getPercentageComplete() {
        return percentageComplete;
    }

    public void setPercentageComplete(int percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeploymentGuid() {
        return deploymentGuid;
    }

    public void setDeploymentGuid(String deploymentGuid) {
        this.deploymentGuid = deploymentGuid;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getChangesHref() {
        return changesHref;
    }

    public void setChangesHref(String changesHref) {
        this.changesHref = changesHref;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getBuildTypeId() {
        return buildTypeId;
    }

    public void setBuildTypeId(String buildTypeId) {
        this.buildTypeId = buildTypeId;
    }

    public String getLastChangeId() {
        return lastChangeId;
    }

    public void setLastChangeId(String lastChangeId) {
        this.lastChangeId = lastChangeId;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getCurrentStageText() {
        return currentStageText;
    }

    public void setCurrentStageText(String currentStageText) {
        this.currentStageText = currentStageText;
    }
}
