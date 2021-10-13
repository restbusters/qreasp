package com.restbusters.integraton.swagger;

import com.restbusters.integraton.swagger.model.OperationParameters;
import com.restbusters.integraton.swagger.model.SwaggerApiResource;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.Operation;
import io.swagger.oas.models.PathItem;
import io.swagger.oas.models.Paths;
import io.swagger.oas.models.parameters.Parameter;
import io.swagger.parser.v3.OpenAPIV3Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import v2.io.swagger.models.HttpMethod;
import v2.io.swagger.parser.SwaggerException;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Sasha matsaylo on 2020-09-10
 * @project qreasp
 */
public class SwaggerManager {

    private static SwaggerManager instance;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private SwaggerManager() {
    }

    public static synchronized SwaggerManager getInstance() {
        if (instance == null) {
            instance = new SwaggerManager();
        }
        return instance;
    }


    private List<SwaggerApiResource> buildSwaggerResources(OpenAPI openAPI) {
        List<SwaggerApiResource> apiResourceList = new ArrayList<>();
        Paths path = openAPI.getPaths();
        String serverUrl = openAPI.getServers().get(0).getUrl();
        path.entrySet().forEach(entry -> {
            PathItem pathItem = entry.getValue();
            String resourcePath = serverUrl + entry.getKey();
            if (pathItem.getGet() != null) {
                Operation getOperation = pathItem.getGet();
                SwaggerApiResource getResource = createSwaggerApiResource(getOperation, resourcePath, HttpMethod.GET.name());
                getResource = SwaggerDescriptorHelper.normalizeSwaggerApiResource(getResource, serverUrl);
                apiResourceList.add(getResource);
            }
            if (pathItem.getPost() != null) {
                Operation postOperation = pathItem.getPost();
                SwaggerApiResource postResource = createSwaggerApiResource(postOperation, resourcePath, HttpMethod.POST.name());
                postResource = SwaggerDescriptorHelper.normalizeSwaggerApiResource(postResource, serverUrl);
                apiResourceList.add(postResource);
            }
            if (pathItem.getPut() != null) {
                Operation putOperation = pathItem.getPut();
                SwaggerApiResource putResource = createSwaggerApiResource(putOperation, resourcePath, HttpMethod.PUT.name());
                putResource = SwaggerDescriptorHelper.normalizeSwaggerApiResource(putResource, serverUrl);
                apiResourceList.add(putResource);
            }
            if (pathItem.getPatch() != null) {
                Operation patchOperation = pathItem.getPatch();
                SwaggerApiResource patchResource = createSwaggerApiResource(patchOperation, resourcePath, HttpMethod.PATCH.name());
                patchResource = SwaggerDescriptorHelper.normalizeSwaggerApiResource(patchResource, serverUrl);
                apiResourceList.add(patchResource);
            }
            if (pathItem.getDelete() != null) {
                Operation deleteOperation = pathItem.getDelete();
                SwaggerApiResource deleteResource = createSwaggerApiResource(deleteOperation, resourcePath, HttpMethod.DELETE.name());
                deleteResource = SwaggerDescriptorHelper.normalizeSwaggerApiResource(deleteResource, serverUrl);
                apiResourceList.add(deleteResource);
            }
        });
        return apiResourceList;
    }

    public SwaggerDescriptor getSwaggerDescriptor(String url) {
        OpenAPI openAPI = initOpenApi(url, "HTTP");
        return buildDescriptor(openAPI);
    }


    public SwaggerDescriptor getSwaggerDescriptorFromSwaggerContent(String swaggerContent) {
        OpenAPI openAPI = initOpenApi(swaggerContent, "JSON");
        return buildDescriptor(openAPI);
    }

    private SwaggerDescriptor getNewDescriptor(){
        return new SwaggerDescriptor();
    }

    private OpenAPI initOpenApi(String content, String type){
        OpenAPI openAPI = null;
        if(type.equalsIgnoreCase("HTTP")){
            openAPI = new OpenAPIV3Parser().read(content);
        }
        if(type.equalsIgnoreCase("JSON")){
            openAPI = new OpenAPIV3Parser().readContents(content, null, null).getOpenAPI();
        }
        if(openAPI == null){
            throw new SwaggerException("Failed to build OpenApi");
        }
        return openAPI;
    }

    private SwaggerDescriptor buildDescriptor( OpenAPI openAPI){
        SwaggerDescriptor swaggerDescriptor = new SwaggerDescriptor();
        swaggerDescriptor.setApiTitle(openAPI.getInfo().getTitle());
        swaggerDescriptor.setServerUrl(openAPI.getServers().get(0).getUrl());
        swaggerDescriptor.setApiVersion(openAPI.getOpenapi());
        swaggerDescriptor.setSwaggerApiResources(this.buildSwaggerResources(openAPI));
        return swaggerDescriptor;
    }


    private String getServiceUrl(String serviceUrl){
        String host = null;
        try {
            URL url = new URL(serviceUrl);
            host = url.getProtocol() + "//" + url.getHost();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return host;
    }

    public List<SwaggerDescriptor> getSwaggerApiResources(List<String> swaggerUrls) {
        List<SwaggerDescriptor> swaggerDescriptors = new ArrayList<>();
        swaggerUrls.stream().parallel().forEach(url -> {
            SwaggerDescriptor swaggerDescriptor = null;
            try {
                swaggerDescriptors.add(getSwaggerDescriptor(url));
            } catch (Exception e) {
                logger.error("Failed to obtains swagger resource for url: {}", url);
                e.printStackTrace();
            }
        });
        return swaggerDescriptors;

    }


    private SwaggerApiResource createSwaggerApiResource(Operation operation, String resourcePath, String httpVerb) {
        SwaggerApiResource apiResource = new SwaggerApiResource();
        apiResource.setResourcePath(resourcePath);
        apiResource.setHttpMethod(httpVerb);
        if (operation.getOperationId() != null) {
            apiResource.setOperationId(operation.getOperationId());
        }
        if (operation.getParameters() != null) {
            apiResource.setOperationParameters(setOperationParameter(operation.getParameters()));
        }
        if (!StringUtils.isEmpty(operation.getSummary())) {
            apiResource.setSummary(operation.getSummary());
        }
        if (!StringUtils.isEmpty(operation.getDescription())) {
            apiResource.setDescription(operation.getDescription());
        }
        return apiResource;
    }

    private List<OperationParameters> setOperationParameter(List<Parameter> parameters) {
        List<OperationParameters> operationParametersList = new ArrayList<>();
        parameters.stream().forEach(
                parameter -> {
                    OperationParameters operationParameters = new OperationParameters();
                    operationParameters.setDescription(parameter.getDescription());
                    operationParameters.setIn(parameter.getIn());
                    operationParameters.setName(parameter.getName());
                    if (!StringUtils.isEmpty(parameter.getRequired())) {
                        operationParameters.setRequired(parameter.getRequired());
                    }
                    operationParametersList.add(operationParameters);
                });
        return operationParametersList;
    }

    private void normalizeSwaggerApiResource(SwaggerApiResource swaggerApiResource, String serverUrl) {

        swaggerApiResource.setPathParams(getNormalizedPathParams(swaggerApiResource.getResourcePath()));
    }


    private List<String> getNormalizedPathParams(String path) {

        List<String> params= new ArrayList<>();

        if (isBracesInPath(path)) {

            int openBracePos = path.indexOf("{");

            do {
                int closeBracePos = path.indexOf("}", openBracePos);
                if (closeBracePos == -1) {
                    logger.info("Invalid Resource Path Braces in path: " + path);
                }
                else {
                    params.add(path.substring(openBracePos + 1, closeBracePos));
                    openBracePos = path.indexOf("{", closeBracePos + 1);
                }
            } while(openBracePos != -1);
        }
        return params;
    }


    private boolean isBracesInPath(String path) {
        return path.contains("{") && path.contains("}");
    }

}