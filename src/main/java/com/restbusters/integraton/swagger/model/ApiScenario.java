package com.restbusters.integraton.swagger.model;

import lombok.Data;

import java.util.List;

/**
 * @author Sasha Matsaylo on 2020-12-02
 * @project qreasp
 */
@Data
public class ApiScenario {

    private String name;
    private String descriptor;
    private List<ApiScenarioStep> apiScenarioSteps;
    private String state;
}
