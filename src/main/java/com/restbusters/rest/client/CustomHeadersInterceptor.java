package com.restbusters.rest.client;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * OkHttp Interceptor that adds multiple headers to every request.
 * Use this when you need to set a map of headers on a client.
 *
 * @author qreasp
 */
public class CustomHeadersInterceptor implements Interceptor {

    private final Map<String, String> headers;

    /**
     * Creates an interceptor with the given headers.
     * @param headers Map of header names to values
     */
    public CustomHeadersInterceptor(Map<String, String> headers) {
        this.headers = headers != null ? new HashMap<>(headers) : Collections.emptyMap();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();

        // Add all headers from the map
        for (Map.Entry<String, String> header : headers.entrySet()) {
            requestBuilder.header(header.getKey(), header.getValue());
        }

        return chain.proceed(requestBuilder.build());
    }

    /**
     * Returns an unmodifiable view of the headers.
     */
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
