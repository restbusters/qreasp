package com.restbusters.rest.model;

import java.util.Map;

/**
 * @author Sasha Matsaylo on 7/13/21
 * @project qreasp
 */
public class HttpRequestBuilder {


    private String httpMethod;
    private String url;
    private Map<String,String> urlParams;
    private Map<String,String> queryParams;
    private Map<String,String> headers;
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

    public HttpRestRequest build() {
        HttpRestRequest httpRestRequest = new HttpRestRequest(this.httpMethod, this.url);

        if(this.urlParams != null){
            httpRestRequest.setUrlParams(this.urlParams);
        }

        if(this.queryParams != null){
            httpRestRequest.setQueryParams(this.queryParams);
        }

        if(this.headers != null){
            httpRestRequest.setHeaders(this.headers);
        }

        if(this.requestBody != null){
            httpRestRequest.setRequestBody(this.requestBody);
        }

        if(this.contentType != null){
            httpRestRequest.setContentType(this.contentType);
        }

        return httpRestRequest;

    }
}
