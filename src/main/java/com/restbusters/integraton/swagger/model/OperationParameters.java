package com.restbusters.integraton.swagger.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sasha Matsaylo on 2020-11-22
 * @project qreasp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationParameters {

    private String name;
    private String in;
    private boolean required;
    private String description;
}
