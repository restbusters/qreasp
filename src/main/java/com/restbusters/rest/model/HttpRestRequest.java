package com.restbusters.rest.model;

import lombok.Data;

import java.util.Map;

/**
 * @author Sasha Matsaylo on 2020-11-29
 * @project qreasp
 */
@Data
public class HttpRestRequest {

    private String requestBody;
    private Map<String,String> headers;
    private Map<String,String> urlParams;
    private Map<String,String> queryParams;
    private String url;
    private String httpMethod;
    private String contentType;

}
