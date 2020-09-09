package com.restbusters.rest.util.deployment.model.manifest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author smatsaylo on 2019-12-10
 * @project dart
 */


public class PipelineExecution extends ManifestModel {


    private Long id;


    @JsonProperty("product")

    private String product;


    @JsonProperty("project")

    private String project;


    @JsonProperty("env")

    private String env;


    @JsonProperty("state")

    private String state;


    @JsonProperty("status")

    private String status;


    @JsonProperty("deploymentGuid")

    private String deploymentGuid;


    private String runIdGuid;


    @JsonProperty("PostDeployJob")

    private List<PostDeployExecution> postDeployExecutions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDeploymentGuid() {
        return deploymentGuid;
    }

    public void setDeploymentGuid(String deploymentGuid) {
        this.deploymentGuid = deploymentGuid;
    }

    public List<PostDeployExecution> getPostDeployExecutions() {
        return postDeployExecutions;
    }

    public void setPostDeployExecutions(List<PostDeployExecution> postDeployExecutions) {
        this.postDeployExecutions = postDeployExecutions;
    }

    public String getRunIdGuid() {
        return runIdGuid;
    }

    public void setRunIdGuid(String runIdGuid) {
        this.runIdGuid = runIdGuid;
    }
}
