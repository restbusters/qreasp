package com.restbusters.integraton.swagger.model;

import lombok.Data;

/**
 * @author Sasha Matsaylo on 2020-12-02
 * @project qreasp
 */
@Data
public class ApiScenarioStep {

    private HttpRestRequest httpRestRequest;
    private SubstitutionRules substitutionRules;
    private HttpRestResponse httpRestResponse;
}
