package com.restbusters.integraton.swagger.model;

import lombok.Data;

/**
 * @author Sasha Matsaylo on 2020-11-22
 * @project qreasp
 */
@Data
public class OperationParameters {

    private String name;
    private String in;
    private boolean required;
    private String description;
}
