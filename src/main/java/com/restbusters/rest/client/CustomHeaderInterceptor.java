package com.restbusters.rest.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class CustomHeaderInterceptor implements Interceptor {

    private final String headerName;
    private final String headerValue;

    public CustomHeaderInterceptor(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request requestWithHeader = original.newBuilder()
                .header(headerName, headerValue)
                .build();
        return chain.proceed(requestWithHeader);
    }
}