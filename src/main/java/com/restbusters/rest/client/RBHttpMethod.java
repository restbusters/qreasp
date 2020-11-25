package com.restbusters.rest.client;

/**
 * @author amatsaylo on 9/17/19
 * @project qreasp
 */
public interface RBHttpMethod {
    String GET = "GET";
    String POST = "POST";
    String PUT = "PUT";
    String DELETE = "DELETE";
    String HEAD = "HEAD";
    String OPTIONS = "OPTIONS";
    String PATCH = "PATCH";
    String value();
}
