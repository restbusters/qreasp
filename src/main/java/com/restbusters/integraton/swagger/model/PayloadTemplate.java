package com.restbusters.integraton.swagger.model;

import lombok.Data;


/**
 * @author Sasha Matsaylo on 2020-11-29
 * @project qreasp
 */
@Data
public class PayloadTemplate extends HttpRest {
    private String name;
    private String type;
    private String payload;
    private String description;
}
