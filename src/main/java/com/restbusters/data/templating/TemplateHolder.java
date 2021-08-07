package com.restbusters.data.templating;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sasha Matsaylo on 8/6/21
 * @project qreasp
 */
@Setter
@Getter
public class TemplateHolder {

    private String name;
    private String inputFileName;
    private String description;
    private String version;
    private String template;
    private String input;
}
