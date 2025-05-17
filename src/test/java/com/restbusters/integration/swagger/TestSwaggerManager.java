package com.restbusters.integration.swagger;

import com.restbusters.exception.RecordNotFound;
import com.restbusters.integraton.swagger.OpenApiV3Manager;
import com.restbusters.integraton.swagger.SwaggerApiResourceFilter;
import com.restbusters.integraton.swagger.SwaggerDescriptorHelper;
import com.restbusters.integraton.swagger.SwaggerManager;
import com.restbusters.integraton.swagger.model.SwaggerApiResource;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import com.restbusters.integraton.swagger.model.OpenApiParseException;
import com.restbusters.util.common.RBFileUtils;
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
public class TestSwaggerManager {

    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String swaggerUrl = "https://petstore.swagger.io/v2/swagger.json";
    private final String openApiUrl = "https://petstore3.swagger.io/api/v3/openapi.json";
    private List<String> swaggerUrls;
    private List<SwaggerDescriptor> swaggerDescriptors;
    private final int swaggerSize = 3;
    private String jsonContent;


    @BeforeClass
    private void setUp() {
        swaggerUrls = new ArrayList<>();
        this.swaggerDescriptors = new ArrayList<>();
        this.setUrls();
        this.jsonContent = RBFileUtils.getFileOnClassPathAsString("swagger/open-api.json");
    }

    private void setUrls(){
        for(int i=1; i<swaggerSize; i++){
            this.swaggerUrls.add(this.swaggerUrl);
        }
    }


    @Test()
    public void build_swagger_resource(){
        Assert.assertNotNull(SwaggerManager.getInstance().getSwaggerDescriptor(swaggerUrl));
    }

    @Test()
    public void build_open_api_resource_from_url(){
      SwaggerDescriptor swaggerDescriptor = null;
        try{
           swaggerDescriptor = OpenApiV3Manager.getInstance().getSwaggerDescriptorFromUrl(openApiUrl);
        }
        catch (Exception e) {
          logger.error("Failed to obtains swagger resource for url: {}", openApiUrl);
          e.printStackTrace();
        }
        Assert.assertNotNull(swaggerDescriptor);
    }

    @Test(enabled = true)
    public void build_swagger_descriptor(){
        SwaggerDescriptor swaggerDescriptor = null;
        try{
           swaggerDescriptor = SwaggerManager.getInstance().getSwaggerDescriptor(swaggerUrl);
         }
         catch (Exception e) {
           logger.error("Failed to obtains swagger resource for url: {}", swaggerUrl);
           e.printStackTrace();
        }
        Assert.assertNotNull(swaggerDescriptor);
        this.swaggerDescriptors.add(swaggerDescriptor);
    }

    @Test(enabled = true)
    public void build_open_api_descriptor(){
        SwaggerDescriptor swaggerDescriptor = null;
        try{
         swaggerDescriptor = OpenApiV3Manager.getInstance().getSwaggerDescriptorFromUrl(openApiUrl);
        }
        catch (Exception e) {
           logger.error("Failed to obtains swagger resource for url: {}", openApiUrl);
           e.printStackTrace();
        }
        Assert.assertNotNull(swaggerDescriptor);
        this.swaggerDescriptors.add(swaggerDescriptor);

    }

    @Test(enabled = true)
    public void build_swagger_from_list(){
        List<SwaggerDescriptor> swaggerDescriptor = SwaggerManager.getInstance().getSwaggerApiResources(this.swaggerUrls);
        Assert.assertNotNull(swaggerDescriptor);
        Assert.assertTrue(swaggerDescriptor.size() == this.swaggerSize - 1);

    }

    @Test(enabled = true, dependsOnMethods = "build_open_api_descriptor")
    public void build_swagger_filter() throws RecordNotFound {
        SwaggerApiResource swaggerApiResource = SwaggerApiResourceFilter.fetchApiResource(this.swaggerDescriptors, "Swagger Petstore - OpenAPI 3.0", "addPet");
        Assert.assertNotNull(swaggerApiResource);
    }

    @Test(enabled = true)
    public void build_swagger_from_json_content(){
        SwaggerDescriptor swaggerDescriptor = SwaggerManager.getInstance().getSwaggerDescriptorFromSwaggerContent(this.jsonContent);
        Assert.assertNotNull(swaggerDescriptor);
    }

    @Test(enabled = true)
    public void build_descriptor_with_swagger_helper(){
        SwaggerDescriptorHelper.getInstance().initNoneAuthSwaggers(this.swaggerUrls);
        Assert.assertTrue(SwaggerDescriptorHelper.getInstance().getSwaggerDescriptor().size() == this.swaggerUrls.size());
    }

    @Test(enabled = true)
    public void swagger_helper_search_api_resource() throws RecordNotFound {
        SwaggerDescriptorHelper.getInstance().initNoneAuthSwaggers(this.swaggerUrls);
        Assert.assertNotNull(SwaggerDescriptorHelper.getInstance().fetchApiResource("Swagger Petstore", "Finds Pets by status"));
    }





}
