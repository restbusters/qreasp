package com.restbusters.rest.util.deployment.model.manifest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class Manifest extends ManifestModel {


    private Long id;


    @JsonProperty("project")
    private String project;


    @JsonProperty("env")
    private String env;


    @JsonProperty("version")
    private String version;


    private String deploymentGuid;


    private String releaseNotes;


    private String gitSha;


    private String commitMessage;


    private String commitAuthor;

    public String getCommitAuthor() {
        return commitAuthor;
    }

    public void setCommitAuthor(String commitAuthor) {
        this.commitAuthor = commitAuthor;
    }


    private long pipelineId;


    @JsonProperty("services")

    private List<ServiceItem> services;


    @JsonProperty("deploy")

    private List<ManifestDeployment> manifestDeployment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getProject() {
        return project;
    }

    public void setServices(List<ServiceItem> services) {
        this.services = services;
    }

    public List<ServiceItem> getServices() {
        return services;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getEnv() {
        return env;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public String getDeploymentGuid() {
        return deploymentGuid;
    }

    public void setDeploymentGuid(String deploymentGuid) {
        this.deploymentGuid = deploymentGuid;
    }

    public List<ManifestDeployment> getManifestDeployment() {
        return manifestDeployment;
    }

    public void setManifestDeployment(List<ManifestDeployment> manifestDeployment) {
        this.manifestDeployment = manifestDeployment;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public String getGitSha() {
        return gitSha;
    }

    public void setGitSha(String gitSha) {
        this.gitSha = gitSha;
    }

    public long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }
}
