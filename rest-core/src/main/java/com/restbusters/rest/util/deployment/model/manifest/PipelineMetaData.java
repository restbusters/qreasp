package com.restbusters.rest.util.deployment.model.manifest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author smatsaylo on 2019-12-10
 * @project dart
 */

public class PipelineMetaData extends ManifestModel {


    private Long id;


    @JsonProperty("product")

    private String product;


    @JsonProperty("project")

    private String project;


    @JsonProperty("env")

    private String env;


    @JsonProperty("metadata")

    private String metadata;

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

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
