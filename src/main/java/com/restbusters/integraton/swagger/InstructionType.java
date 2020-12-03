package com.restbusters.integraton.swagger;

/**
 * @author Sasha Matsaylo on 2020-12-02
 * @project qreasp
 */
public enum InstructionType {

    FROM_RESPONSE("FROM_RESPONSE"),
    USER_DEFINED("USER_DEFINED");

    private String instructionType;

    InstructionType(String instructionType) {
        this.instructionType = instructionType;
    }

    public String instructionType() {
        return instructionType;
    }
}
