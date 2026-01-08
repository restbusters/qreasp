package com.restbusters.integraton.swagger.model;

/**
 * @author Sasha Matsaylo on 2020-09-14
 * @project qreasp
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerApiResource {

    @JsonProperty("operationParameters")
    private List<OperationParameters> operationParameters;

    @JsonProperty("pathParams")
    private List<String> pathParams;

    @JsonProperty("resourcePath")
    private String resourcePath;

    @JsonProperty("operationId")
    private String operationId;

    @JsonProperty("httpMethod")
    private String httpMethod;

    @JsonProperty("body")
    private String body;

    @JsonProperty
    private String summary;

    @JsonProperty
    private String description;

}