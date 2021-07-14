package com.restbusters.rest.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author Sasha Matsaylo on 2020-11-29
 * @project qreasp
 */
@Getter
@Setter
//@Builder(toBuilder = true)
public class HttpRestRequest {

    private String requestBody;
    private Map<String,String> headers;
    private Map<String,String> urlParams;
    private Map<String,String> queryParams;
    private String url;
    private String httpMethod;
    private String contentType;

}
