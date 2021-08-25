package com.restbusters.integraton.swagger;

import com.restbusters.exception.RecordNotFound;
import com.restbusters.integraton.swagger.model.SwaggerApiResource;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sasha Matsaylo on 8/22/21
 * @project qreasp
 */
public class SwaggerDescriptorHelper {

    private static SwaggerDescriptorHelper instance;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private List<SwaggerDescriptor> swaggerDescriptors = new ArrayList<>();


    public SwaggerDescriptorHelper(){
    }

    public static synchronized SwaggerDescriptorHelper getInstance() {
        if (instance == null) {
            instance = new SwaggerDescriptorHelper();
        }
        return instance;
    }

    public void initNoneAuthSwaggers(List<String> swaggerUrls){
        for(String url : swaggerUrls){
            SwaggerDescriptor swaggerDescriptor = SwaggerManager.getInstance().getSwaggerDescriptor(url);
            if(swaggerDescriptor != null){
                logger.info("Setting swagger descriptor for url: " + url);
                this.swaggerDescriptors.add(swaggerDescriptor);
            }
            else {
                logger.error("Failed to build swagger descriptor for url: " + url);
            }
        }
    }

    /**
     * <p>This is a simple description of the method. . .
     * <a href="https://github.com/restbusters/qreasp">Build from real world cases!</a>
     * </p>
     * @param apiTitle the value of Swagger title exactly as it appears in swagger
     * @param operationIdOrSummary the operationId or summary exactly as it appears in json schema or swagger ui
     * @return api resource metadata from swagger
     * @since 0.0.32
     */
    public SwaggerApiResource fetchApiResource(String apiTitle, String operationIdOrSummary) throws RecordNotFound {
        if(StringUtils.isBlank(apiTitle)){
            throw new IllegalArgumentException("Invalid argument apiTitle: " + apiTitle);
        }
        if(StringUtils.isBlank(apiTitle)){
            throw new IllegalArgumentException("Invalid argument operationIdOrSummary: " + operationIdOrSummary);
        }
        return SwaggerApiResourceFilter.fetchApiResource(this.swaggerDescriptors, apiTitle, operationIdOrSummary);
    }

    public void resetSwaggerDescriptor(List<SwaggerDescriptor>swaggerDescriptors){
        this.swaggerDescriptors = swaggerDescriptors;

    }

    public List<SwaggerDescriptor> getSwaggerDescriptor(){
        return this.swaggerDescriptors;
    }



}
