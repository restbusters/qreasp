package com.restbusters.rest.util.deployment.model.manifest;

import com.fasterxml.jackson.annotation.JsonProperty;


public class DeploymentGuid {

    @JsonProperty("deploymentGuid")
    private String deploymentGuid;

    public void setDeploymentGuid(String deploymentGuid) {
        this.deploymentGuid = deploymentGuid;
    }

    public String getDeploymentGuid() {
        return deploymentGuid;
    }

    @Override
    public String toString() {
        return
                "DeploymentGuid{" +
                        "deploymentGuid = '" + deploymentGuid + '\'' +
                        "}";
    }
}