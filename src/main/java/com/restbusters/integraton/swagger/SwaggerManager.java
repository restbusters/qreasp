package com.restbusters.integraton.swagger;

import com.restbusters.integration.swagger.model.SwaggerApiResource;
import com.restbusters.integraton.swagger.model.*;
import com.restbusters.rest.client.RestClientHelper;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * @author Sasha Matsaylo on 2020-11-26
 * @project qreasp
 */
public class SwaggerManager {

    private List<SwaggerDescriptor> swaggerDescriptor;
    private List<Map<String, Object>> swaggerConfig;
    private SwaggerUrl swaggerUrl;
    private OkHttpClient noAuthClient;
    private PayloadManager payloadManager;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public SwaggerManager(SwaggerUrl swaggerUrl){
        this.noAuthClient = RestClientHelper.getInstance().buildNoAuthClient();
        this.swaggerUrl = swaggerUrl;
        this.swaggerDescriptor = new ArrayList<>();
        setSwaggerDescriptor();
    }




    private void setSwaggerDescriptor() {

        Collections.synchronizedList(this.swaggerUrl.getSwaggerUrls()).stream().parallel().forEach(url -> {
            try {
                Response response = RestClientHelper.getInstance().doGetRequest(this.noAuthClient, url.toString(), null, null);
                if (response.isSuccessful()) {
                    String body = response.body().string().replaceAll("\\s", "");
                    if (body.matches(".*\"swagger.+\\:.+(\\d).*")) {
                        logger.info("The requested url {} is swagger");
                        //grebaniy swagger does not work with getContent but works with readUrl method
                        SwaggerDescriptor swaggerDescriptor = SwaggerHelper.getInstance().getSwaggerDescriptor(url.toString());
                        swaggerDescriptor.setApiType("swagger");
                        this.swaggerDescriptor.add(swaggerDescriptor);
                    }
                    if (body.matches(".*\"openapi.+\\:.+(\\d).*")) {
                        //swaggerDescriptor.setApiType("swagger");
                        logger.info("The requested url {} is openapi");
                        SwaggerDescriptor swaggerDescriptor = OpenApiHelper.getInstance().getSwaggerDescriptor(body);
                        swaggerDescriptor.setApiType("openapi");
                        this.swaggerDescriptor.add(swaggerDescriptor);
                    }
                }

            } catch (IOException e) {
                logger.error("Failed to obtain swagger string from url {} {}", url, e.getLocalizedMessage());
                e.printStackTrace();
            }
        });

    }

    public List<SwaggerDescriptor> getSwaggerDescriptor() {
        return swaggerDescriptor;
    }

    public SwaggerApiResource findSwaggerResource(String apiTitle, String operationId) {
        SwaggerDescriptor swaggerDescriptor = findSwaggerDescriptor(apiTitle);
        if (swaggerDescriptor == null) {
            return null;
        }
        return swaggerDescriptor.getSwaggerApiResources()
                .stream()
                .filter(apiResource -> apiResource.getOperationId().equalsIgnoreCase(operationId))
                .findAny().orElse(null);
    }

    public SwaggerDescriptor findSwaggerDescriptor(String apiTitle) {
        return this.swaggerDescriptor.stream()
                .filter(descriptor -> descriptor.getApiTitle().equalsIgnoreCase(apiTitle))
                .findFirst()
                .orElse(null);
    }

    public boolean setNoneAuthHttpClientForSwagger(String swaggerTitle, Map<String, String> headers) {
        SwaggerDescriptor swaggerDescriptor = findSwaggerDescriptor(swaggerTitle);
        if (swaggerDescriptor != null) {
            OkHttpClient okHttpClient = RestClientHelper.getInstance().buildNoAuthClient(headers);
            swaggerDescriptor.setHttpClient(okHttpClient);
            return true;
        }
        return false;
    }

    private OkHttpClient getHttpClient(String swaggerTitle) {
        SwaggerDescriptor swaggerDescriptor = findSwaggerDescriptor(swaggerTitle);
        if (swaggerDescriptor != null) {
            return swaggerDescriptor.getHttpClient();
        }
        return null;
    }

    public void setPayloadManager(PayloadManager payloadManager) {
        this.payloadManager = payloadManager;
    }

    public String getPayload(String swaggerTitle, String swaggerOperationId, @Nullable String payLoadType, Map payload){
        return payloadManager.getPayload(swaggerTitle, swaggerOperationId, payLoadType, payload);
    }

    public HttpRestResponse executeSwaggerEndPoint(HttpRestRequest httpRestRequest) {
        //put protection if urlParam is not what is expected
        HttpRestResponse httpRestResponse = new HttpRestResponse();
        httpRestResponse.setHttpRestRequest(httpRestRequest);
        SwaggerApiResource swaggerApiResource = findSwaggerResource(httpRestRequest.getApiTitle(), httpRestRequest.getOperationId());
        if (swaggerApiResource == null) {
            httpRestResponse.setStatus("ABORTED");
            httpRestResponse.setReason("OPERATION NOT FOUND");
            httpRestResponse.setHttpRestRequest(httpRestRequest);
            return httpRestResponse;
        }
        httpRestRequest.setUrl(swaggerApiResource.getResourcePath());
        OkHttpClient okHttpClient = this.getHttpClient(httpRestRequest.getApiTitle());
        if (okHttpClient == null) {
            httpRestResponse.setStatus("ABORTED");
            httpRestResponse.setReason("HTTP CLIENT NOT SET");
            httpRestResponse.setHttpRestRequest(httpRestRequest);
            return httpRestResponse;
        }

        Response response;
        switch (swaggerApiResource.getHttpMethod()) {
            case "GET":
                try {
                    response = RestClientHelper.getInstance().doGetRequest(okHttpClient, httpRestRequest);
                    httpRestResponse.setResponseBody(response.body().string());
                    httpRestResponse.setHttpCode(response.code());
                    httpRestResponse.setStatus("FINISHED");
                    return httpRestResponse;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            case "POST":
                try {
                    response = RestClientHelper.getInstance().doPostRequest(okHttpClient, httpRestRequest, null);
                    httpRestResponse.setResponseBody(response.body().string());
                    httpRestResponse.setHttpCode(response.code());
                    httpRestResponse.setStatus("FINISHED");
                    return httpRestResponse;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            case "PUT":
                try {
                    response = RestClientHelper.getInstance().doPostRequest(okHttpClient, httpRestRequest, null);
                    httpRestResponse.setResponseBody(response.body().string());
                    httpRestResponse.setHttpCode(response.code());
                    httpRestResponse.setStatus("FINISHED");
                    return httpRestResponse;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            default:
                //Java code
                ;
        }
        return httpRestResponse;

    }

}