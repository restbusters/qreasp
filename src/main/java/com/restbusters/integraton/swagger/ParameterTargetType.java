package com.restbusters.integraton.swagger;

/**
 * @author Sasha Matsaylo on 2020-12-02
 * @project qreasp
 */
public enum ParameterTargetType {

    REQUEST_BODY("REQUEST_BODY"),
    REQUEST_URL("REQUEST_URL"),
    REQUEST_QUERY("REQUEST_QUERY");

    private String parameterTargetType;

    ParameterTargetType(String parameterTargetType) {
        this.parameterTargetType = parameterTargetType;
    }

    public String parameterTargetType() {
        return parameterTargetType;
    }
}
