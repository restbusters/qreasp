package com.restbusters.rest.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author amatsaylo on 9/17/19
 * @project qreasp
 */

public class PostHeaderInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if(request.url().toString().matches(".*/rest/productdefinition/v1/import-template")){
            Request requestWithHeader = request.newBuilder()
                    .addHeader("Content-Type", "multipart/form-data")
                    .build();
            return chain.proceed(requestWithHeader);
        }
        if (request.method().equalsIgnoreCase(RBHttpMethod.POST) || request.method().equalsIgnoreCase(RBHttpMethod.PATCH)) {
            Request requestWithHeader = request.newBuilder()
                    .header("Content-Type", "application/json").build();
            return chain.proceed(requestWithHeader);
        }
        return chain.proceed(request);
    }

}
