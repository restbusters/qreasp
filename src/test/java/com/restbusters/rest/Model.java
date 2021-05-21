package com.restbusters.rest;

import lombok.Builder;
import lombok.Data;

/**
 * @author Sasha Matsaylo on 5/16/21
 * @project qreasp
 */
@Data
@Builder
class Model {
    private final String name;
    private final int age;
    private final String group;

}
