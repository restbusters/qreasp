package com.restbusters.integraton.swagger.model;

import lombok.Data;

import java.util.List;

/**
 * @author Sasha Matsaylo on 2020-12-02
 * @project qreasp
 */
@Data
public class ApiStep extends OperationMetaData {

    private HttpRestRequest httpRestRequest;
    private List<SubstitutionRule> substitutionRules;
    private HttpRestResponse httpRestResponse;
}
