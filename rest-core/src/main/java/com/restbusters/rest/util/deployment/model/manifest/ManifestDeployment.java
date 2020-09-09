package com.restbusters.rest.util.deployment.model.manifest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author smatsaylo on 2019-12-03
 * @project dart
 */


public class ManifestDeployment extends ManifestModel {


    private Long id;


    private String deploymentGuid;


    private String deploymentState;


    private String deploymentStatus;


    private String triggeredBy;


    private Long manifestId;

    @JsonIgnore
    private Manifest manifest;


    @JsonProperty("deployServices")

    private List<DeployCandidate> deployCandidates;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeploymentGuid() {
        return deploymentGuid;
    }

    public void setDeploymentGuid(String deploymentGuid) {
        this.deploymentGuid = deploymentGuid;
    }

    public String getDeploymentState() {
        return deploymentState;
    }

    public void setDeploymentState(String deploymentState) {
        this.deploymentState = deploymentState;
    }

    public String getDeploymentStatus() {
        return deploymentStatus;
    }

    public void setDeploymentStatus(String deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    public Long getManifestId() {
        return manifestId;
    }

    public void setManifestId(Long manifestId) {
        this.manifestId = manifestId;
    }

    public List<DeployCandidate> getDeployCandidates() {
        return deployCandidates;
    }

    public void setDeployCandidates(List<DeployCandidate> deployCandidates) {
        this.deployCandidates = deployCandidates;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

}
