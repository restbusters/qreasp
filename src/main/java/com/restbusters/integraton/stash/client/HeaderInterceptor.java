package com.restbusters.integraton.stash.client;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;


/**
 * @author Sasha Matsaylo on 9/17/19
 * @project qreasp
 */
public class HeaderInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        return chain.proceed(addHeaders(request.url(), requestBuilder));
    }

    private Request addHeaders(HttpUrl url, Request.Builder requestBuilder){
        if(url.pathSegments().contains("raw")){
            requestBuilder.addHeader("Accept", "*/*");
        }
        else {
            requestBuilder.addHeader("Accept", "application/json");
        }
        return requestBuilder.build();
    }

}
