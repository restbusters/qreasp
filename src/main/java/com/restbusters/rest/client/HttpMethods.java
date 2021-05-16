package com.restbusters.rest.client;

/**
 * @author Sasha Matsaylo on 5/14/21
 * @project qreasp
 */
public enum HttpMethods {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS");

    private final String value;

    private HttpMethods(String value){
        this.value = value;
    }

    public static HttpMethods findByValue(String value){
        for(HttpMethods httpMethod : values()){
            if(httpMethod.value.equalsIgnoreCase(value)){
                return httpMethod;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
