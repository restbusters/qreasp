package com.restbusters.integration.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.exception.SwaggerTitleNotSet;
import com.restbusters.integraton.swagger.SwaggerHelper;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import com.restbusters.resource.GlobalResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sasha Matsaylo on 2020-10-02
 * @project qreasp
 */
public class TestSwaggerHelper {

    private SwaggerHelper swaggerHelper = SwaggerHelper.getInstance();
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String url = "https://petstore.swagger.io/v2/swagger.json";
    private final List<String> swaggerUrls = new ArrayList<>();
    private final ObjectMapper objectMapper = GlobalResourceManager.getInstance().getObjectMapper();



    @BeforeClass
    private void setUp() {
        swaggerUrls.add(this.url);
        swaggerUrls.add(this.url + "s");
    }


    @Test(enabled = true)
    public void build_api_resource() throws SwaggerTitleNotSet {
        SwaggerDescriptor swaggerDescriptor = this.swaggerHelper.getSwaggerDescriptor(url);
        Assert.assertEquals(swaggerDescriptor.getApiTitle(), "Swagger Petstore");
        try {
            logger.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(swaggerDescriptor));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test(enabled = true)
    public void build_api_resources_list() {
        List<SwaggerDescriptor> swaggerDescriptors = this.swaggerHelper.getSwaggerApiResources(swaggerUrls);
        Assert.assertEquals(swaggerDescriptors.size(), 1, "2 swagger descriptors");

    }

}
