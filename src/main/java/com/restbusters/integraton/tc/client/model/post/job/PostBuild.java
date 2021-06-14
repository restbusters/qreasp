package com.restbusters.integraton.tc.client.model.post.job;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostBuild {

    @JsonProperty("buildType")
    private BuildType buildType;

    @JsonProperty("branchName")
    private String branchName;

    @JsonProperty("comment")
    private Comment comment;

    @JsonProperty("properties")
    private Properties properties;

}