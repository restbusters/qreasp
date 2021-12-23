package com.restbusters.integraton.tc.client.model.post.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BuildType {

    @JsonProperty("id")
    private String buildTypeId;

    @JsonProperty("projectId")
    private String projectId;

}