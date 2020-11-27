package com.restbusters.integraton.swagger;

import com.google.common.collect.Lists;
import com.restbusters.integraton.swagger.model.OperationParameters;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import com.restbusters.rest.client.RestClientHelper;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private OkHttpClient noAuthClient;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public SwaggerManager(List<Map<String, Object>> swaggerConfig) {
        this.noAuthClient = RestClientHelper.getInstance().buildNoAuthClient();
        this.swaggerConfig = swaggerConfig;
        this.swaggerDescriptor = new ArrayList<>();
        setSwaggerDescriptor();
    }

    private void setSwaggerDescriptor() {
        List<OperationParameters> operationParametersList = new ArrayList<>();
        Collections.synchronizedList(this.swaggerConfig).stream().parallel().forEach(
                swagger -> {
                    List<Object> swaggerUrls = (List<Object>) Arrays.asList(swagger.get("url")).get(0);
                    if(swaggerUrls.size() > 0){
                        Collections.synchronizedList(swaggerUrls).stream().parallel().forEach( url -> {
                            try {
                                Response response = RestClientHelper.getInstance().doGetRequest(this.noAuthClient, url.toString(), null, null);
                                if (response.isSuccessful()) {
                                    String body = response.body().string().replaceAll("\\s", "");
                                    if(body.matches(".*\"swagger.+\\:.+(\\d).*")){
                                        logger.info("The requested url {} is swagger");
                                        SwaggerDescriptor swaggerDescriptor = SwaggerHelper.getInstance().getSwaggerDescriptor(body);
                                        swaggerDescriptor.setApiType("swagger");
                                        this.swaggerDescriptor.add(swaggerDescriptor);
                                    }
                                    if(body.matches(".*\"openapi.+\\:.+(\\d).*")){
                                        //swaggerDescriptor.setApiType("swagger");
                                        logger.info("The requested url {} is openapi");
                                        SwaggerDescriptor swaggerDescriptor = OpenApiHelper.getInstance().getSwaggerDescriptor(body);
                                        swaggerDescriptor.setApiType("openapi");
                                        this.swaggerDescriptor.add(swaggerDescriptor);
                                    }
                                }

                            } catch (IOException e) {
                                logger.error("Failed to obtain swagger string from url {} {}", swagger.get("url").toString(), e.getLocalizedMessage());
                                e.printStackTrace();
                            }
                        });
                    }
                });
    }

    public List<SwaggerDescriptor> getSwaggerDescriptor() {
        return swaggerDescriptor;
    }
}
