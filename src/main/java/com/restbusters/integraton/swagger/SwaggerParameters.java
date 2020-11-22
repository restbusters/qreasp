package com.restbusters.integraton.swagger;

/**
 * @author Sasha Matsaylo on 2020-11-22
 * @project qreasp
 */
public enum SwaggerParameters {

    QUERY ("query"),
    PATH ("path");

    private final String paramType;

    SwaggerParameters(String paramType) {
        this.paramType = paramType;
    }

    public String get() {
        return this.paramType;
    }
}
