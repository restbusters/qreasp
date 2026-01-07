package com.restbusters.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author Sasha Matsaylo on 2020-11-29
 * @project qreasp
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequest {

    private String httpMethod;
    private String url;
    private String uri;
    private Map<String, String> urlParams;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private String requestBody;
    private String contentType;

    public HttpRequest(String httpMethod, String url) {
        this.httpMethod = httpMethod;
        this.url = url;
    }
}
