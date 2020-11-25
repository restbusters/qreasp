package com.restbusters.rest.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author amatsaylo on 9/17/19
 * @project qreasp
 */

public class BearerAuthInterceptor implements Interceptor {

    private String userToken;

    public BearerAuthInterceptor(String userToken) {
        this.userToken = userToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", "Bearer " + userToken)
                .build();
        return chain.proceed(authenticatedRequest);
    }

}
