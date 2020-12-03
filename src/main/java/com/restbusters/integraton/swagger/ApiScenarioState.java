package com.restbusters.integraton.swagger;

/**
 * @author Sasha Matsaylo on 2020-12-02
 * @project qreasp
 */
public enum ApiScenarioState {

    STARTED("STARTED"),
    FINISHED("FINISHED"),
    ABORTED("ABORTED");

    private String apiScenarioState;

    ApiScenarioState(String apiScenarioState) {
        this.apiScenarioState = apiScenarioState;
    }

    public String apiScenarioState() {
        return apiScenarioState;
    }
}
