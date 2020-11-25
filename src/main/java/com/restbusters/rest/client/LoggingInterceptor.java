package com.restbusters.rest.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;


/**
 * @author amatsaylo on 9/17/19
 * @project qreasp
 */
public class LoggingInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        logger.info(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

        okhttp3.Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        logger.info(String.format("Received response for %s with %d in %.1fms%n%s", response.request().url(), response.code(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }


}
