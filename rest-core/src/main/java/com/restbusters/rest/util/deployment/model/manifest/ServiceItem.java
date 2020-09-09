package com.restbusters.rest.util.deployment.model.manifest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class ServiceItem extends ManifestModel {


    private Long id;

    @JsonProperty("infoUrl")
    private String infoUrl;

    @JsonProperty("gitRepoUrl")
    private String gitRepoUrl;

    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;

    @JsonProperty("swaggerUrl")
    private String swaggerUrl;

    @JsonProperty("gitBranch")
    private String gitBranch;


    private Long manifestId;


    private String sampleBuildConfigId;


    private String sampleProjectId;


    private String stashProject;


    private String stashRepo;

    @JsonProperty("healthCheckUrl")
    private String healthCheckUrl;

    @JsonProperty("jobProperties")

    //
    private String jobProperties;


    @JsonIgnore
    private Manifest manifest;


    private List<DeployCandidate> deployServices;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setGitRepoUrl(String gitRepoUrl) {
        this.gitRepoUrl = gitRepoUrl;
    }

    public String getGitRepoUrl() {
        return gitRepoUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setSwaggerUrl(String swaggerUrl) {
        this.swaggerUrl = swaggerUrl;
    }

    public String getSwaggerUrl() {
        return swaggerUrl;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public Long getManifestId() {
        return manifestId;
    }

    public void setManifestId(Long manifestId) {
        this.manifestId = manifestId;
    }

    public String getSampleBuildConfigId() {
        return sampleBuildConfigId;
    }

    public void setSampleBuildConfigId(String sampleBuildConfigId) {
        this.sampleBuildConfigId = sampleBuildConfigId;
    }

    public String getSampleProjectId() {
        return sampleProjectId;
    }

    public void setSampleProjectId(String sampleProjectId) {
        this.sampleProjectId = sampleProjectId;
    }

    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }

    public void setHealthCheckUrl(String healthCheckUrl) {
        this.healthCheckUrl = healthCheckUrl;
    }

    public String getJobProperties() {
        return jobProperties;
    }

    public void setJobProperties(String jobProperties) {
        this.jobProperties = jobProperties;
    }

    public String getStashProject() {
        return stashProject;
    }

    public void setStashProject(String stashProject) {
        this.stashProject = stashProject;
    }

    public String getStashRepo() {
        return stashRepo;
    }

    public void setStashRepo(String stashRepo) {
        this.stashRepo = stashRepo;
    }

    @Override
    public String toString() {
        return
                "ServiceItem{" +
                        "infoUrl = '" + infoUrl + '\'' +
                        ",gitRepoUrl = '" + gitRepoUrl + '\'' +
                        ",name = '" + name + '\'' +
                        ",version = '" + version + '\'' +
                        ",swaggerUrl = '" + swaggerUrl + '\'' +
                        ",gitBranch = '" + gitBranch + '\'' +
                        "}";
    }
}