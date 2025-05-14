package com.restbusters.integraton.stash.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.restbusters.rest.model.HttpRequest;
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

    private HttpRequest getCommitsInRange;
    private HttpRequest getTags;
    private HttpRequest getManifestFileContent;
    private HttpRequest getLastXCommits;
    private HttpRequest getCommitByHash;
    private HttpRequest getCommitsInRangeApiV2;
    private HttpRequest getFileContent;
}
