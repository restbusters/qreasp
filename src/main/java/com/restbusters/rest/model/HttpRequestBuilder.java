package com.restbusters.rest.model;

import java.util.Map;

/**
 * @author Sasha Matsaylo on 7/13/21
 * @project qreasp
 * @deprecated Use {@link HttpRequest#builder()} instead. This class is kept for backward compatibility.
 */
@Deprecated
public class HttpRequestBuilder {

    private String httpMethod;
    private String url;
    private Map<String, String> urlParams;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private String requestBody;
    private String contentType;

    public HttpRequestBuilder(String httpMethod, String url) {
        this.httpMethod = httpMethod;
        this.url = url;
    }

    public HttpRequestBuilder setUrlParams(Map<String, String> urlParams) {
        this.urlParams = urlParams;
        return this;
    }

    public HttpRequestBuilder setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public HttpRequestBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequestBuilder setRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public HttpRequestBuilder setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpRequest build() {
        return HttpRequest.builder()
                .httpMethod(this.httpMethod)
                .url(this.url)
                .urlParams(this.urlParams)
                .queryParams(this.queryParams)
                .headers(this.headers)
                .requestBody(this.requestBody)
                .contentType(this.contentType)
                .build();
    }
}
