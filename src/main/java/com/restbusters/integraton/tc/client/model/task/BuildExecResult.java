package com.restbusters.integraton.tc.client.model.task;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sasha Matsaylo on 12/25/21
 * @project qreasp
 */
@Setter
@Getter
public class BuildExecResult {

    private String state;
    private String executionMetaData;
    private String errors;
    private String buildId;
}
