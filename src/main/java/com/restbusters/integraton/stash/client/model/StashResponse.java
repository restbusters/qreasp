package com.restbusters.integraton.stash.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Sasha Matsaylo on 6/19/21
 * @project qreasp
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StashResponse {
    private int httpStatus;
    private String RequestBody;
    private String errorReason;
}
