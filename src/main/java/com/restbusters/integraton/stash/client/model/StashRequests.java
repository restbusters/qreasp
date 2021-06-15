package com.restbusters.integraton.stash.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.restbusters.rest.model.HttpRestRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author smatsaylo on 6/2/21
 * @project stash-client
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class StashRequests {

    private HttpRestRequest getCommitsInRange;
    private HttpRestRequest getTags;
    private HttpRestRequest getManifestFileContent;
    private HttpRestRequest getLastXCommits;
    private HttpRestRequest getCommitByHash;
    private HttpRestRequest getCommitsInRangeApiV2;
}
