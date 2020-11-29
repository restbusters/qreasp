package com.restbusters.integraton.swagger.model;

import lombok.Data;
import com.restbusters.integration.swagger.model.SwaggerApiResource;


/**
 * @author Sasha Matsaylo on 2020-11-29
 * @project qreasp
 */
@Data
public class HttpRestResponse {
    private int httpCode;
    private String responseBody;
    private HttpRestRequest httpRestRequest;
    private String status;
    private String reason;
}
