package com.restbusters.integration.swagger;

import com.restbusters.integraton.swagger.OpenApiHelper;
import com.restbusters.integraton.swagger.SwaggerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sasha Matsaylo on 2020-10-02
 * @project qreasp
 */
public class TestSwaggerHelper {

    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String swaggerUrl = "https://petstore.swagger.io/v2/swagger.json";
    private final String openApiUrl = "https://petstore3.swagger.io/api/v3/openapi.json";
    private List<String> swaggerUrls;


    @BeforeClass
    private void setUp() throws IOException {

        swaggerUrls = new ArrayList<>();
        swaggerUrls.add(swaggerUrl);
        swaggerUrls.add(openApiUrl);
    }


    @Test()
    public void build_swagger_resource(){
        Assert.assertNotNull(SwaggerHelper.getInstance().getSwaggerDescriptor(swaggerUrl));
    }

    @Test()
    public void build_openapi_resource_from_url(){
        Assert.assertNotNull(OpenApiHelper.getInstance().getSwaggerDescriptorFromUrl(openApiUrl));
    }

    @Test(enabled = true)
    public void build_swagger_descriptor(){
        Assert.assertNotNull(SwaggerHelper.getInstance().getSwaggerDescriptor(swaggerUrl));
    }

    @Test(enabled = true)
    public void build_openapi_descriptor(){
        Assert.assertNotNull(OpenApiHelper.getInstance().getSwaggerDescriptorFromUrl(openApiUrl));
    }





}
