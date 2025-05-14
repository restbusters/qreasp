package com.restbusters.integraton.tc.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.restbusters.rest.model.HttpRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author smatsaylo on 6/2/21
 * @project qreasp
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class TCRequests {

    private HttpRequest getBuilds;
    private HttpRequest getBuildById;
    private HttpRequest getBuildStatisticByBuildId;
    private HttpRequest getBuildChangesByBuildId;
    private HttpRequest postBuild;
}
