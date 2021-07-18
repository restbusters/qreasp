package com.restbusters.integraton.swagger;

import com.restbusters.exception.RecordNotFound;
import com.restbusters.integraton.swagger.model.SwaggerApiResource;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;

import java.util.List;
import java.util.Optional;


/**
 * @author Sasha Matsaylo on 7/16/21
 * @project qreasp
 */
public class SwaggerApiResourceFilter {

    public static SwaggerApiResource fetchApiResource(List<SwaggerDescriptor> swaggerDescriptors, String apiTitle, String operationIdOrSummary) throws RecordNotFound {
        Optional<SwaggerDescriptor> swaggerDescriptor = swaggerDescriptors
                .stream()
                .filter(sd -> sd.getApiTitle().equalsIgnoreCase(apiTitle))
                .findFirst();
        if(!swaggerDescriptor.isPresent()){
            throw new RecordNotFound("Failed to find swagger descriptor by apiTitle " + apiTitle);
        }
        Optional<SwaggerApiResource> swaggerApiResource = swaggerDescriptor.get().getSwaggerApiResources()
                .stream()
                .filter(sd -> sd.getOperationId().equalsIgnoreCase(operationIdOrSummary))
                .findFirst();
        if(swaggerApiResource.isPresent()){
            return swaggerApiResource.get();
        }
        swaggerApiResource = swaggerDescriptor.get().getSwaggerApiResources()
                .stream()
                .filter(sd -> sd.getSummary().equalsIgnoreCase(operationIdOrSummary))
                .findFirst();
        return swaggerApiResource.orElseThrow(() -> new RecordNotFound("Failed to find swagger api resource by " + operationIdOrSummary));

    }
}
