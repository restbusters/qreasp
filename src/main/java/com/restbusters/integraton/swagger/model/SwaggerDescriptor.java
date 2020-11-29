package com.restbusters.integraton.swagger.model;

import lombok.Data;
import okhttp3.OkHttpClient;

import java.util.List;

/**
 * @author Sasha Matsaylo on 2020-11-22
 * @project qreasp
 */

@Data
public class SwaggerDescriptor {

    private String apiTitle;
    private String serverUrl;
    private String apiType;
    private String apiVersion;
    private OkHttpClient httpClient;
    private List<com.restbusters.integration.swagger.model.SwaggerApiResource> swaggerApiResources;
}
