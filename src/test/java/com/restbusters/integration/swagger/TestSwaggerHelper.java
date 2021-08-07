package com.restbusters.integration.swagger;

import com.restbusters.exception.RecordNotFound;
import com.restbusters.integraton.swagger.OpenApiHelper;
import com.restbusters.integraton.swagger.SwaggerApiResourceFilter;
import com.restbusters.integraton.swagger.SwaggerHelper;
import com.restbusters.integraton.swagger.model.SwaggerApiResource;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
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
    private List<SwaggerDescriptor> swaggerDescriptors;
    private final int swaggerSize = 10;


    @BeforeClass
    private void setUp() {
        swaggerUrls = new ArrayList<>();
        this.swaggerDescriptors = new ArrayList<>();
        this.setUrls();
    }

    private void setUrls(){
        for(int i=1; i<swaggerSize; i++){
            this.swaggerUrls.add(this.swaggerUrl);
        }
    }


    @Test()
    public void build_swagger_resource(){
        Assert.assertNotNull(SwaggerHelper.getInstance().getSwaggerDescriptor(swaggerUrl));
    }

    @Test()
    public void build_open_api_resource_from_url(){
        Assert.assertNotNull(OpenApiHelper.getInstance().getSwaggerDescriptorFromUrl(openApiUrl));
    }

    @Test(enabled = true)
    public void build_swagger_descriptor(){
        SwaggerDescriptor swaggerDescriptor = SwaggerHelper.getInstance().getSwaggerDescriptor(swaggerUrl);
        Assert.assertNotNull(swaggerDescriptor);
        this.swaggerDescriptors.add(swaggerDescriptor);
    }

    @Test(enabled = true)
    public void build_open_api_descriptor(){
        SwaggerDescriptor swaggerDescriptor = OpenApiHelper.getInstance().getSwaggerDescriptorFromUrl(openApiUrl);
        Assert.assertNotNull(swaggerDescriptor);
        this.swaggerDescriptors.add(swaggerDescriptor);

    }

    @Test(enabled = true)
    public void build_swagger_from_list(){
        List<SwaggerDescriptor> swaggerDescriptor = SwaggerHelper.getInstance().getSwaggerApiResources(this.swaggerUrls);
        Assert.assertNotNull(swaggerDescriptor);
        Assert.assertTrue(swaggerDescriptor.size() == this.swaggerSize - 1);

    }

    @Test(enabled = true, dependsOnMethods = "build_open_api_descriptor")
    public void build_swagger_filter() throws RecordNotFound {
        SwaggerApiResource swaggerApiResource = SwaggerApiResourceFilter.fetchApiResource(this.swaggerDescriptors, "Swagger Petstore - OpenAPI 3.0", "addPet");
        Assert.assertNotNull(swaggerApiResource);
    }





}
