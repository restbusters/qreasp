package com.restbusters.rest.payload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.payload.model.PayloadTemplate;
import com.restbusters.util.common.FileUtils;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sasha Matsaylo on 2020-11-30
 * @project qreasp
 */
public class FreeMarkerPayloadManager {

    private static FreeMarkerPayloadManager instance;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private String jsonPayloads;
    private List<PayloadTemplate> payloadTemplates;
    private GlobalResourceManager grm = GlobalResourceManager.getInstance();
    private StringTemplateLoader templateLoader = new StringTemplateLoader();
    private Configuration cfg = new Configuration( Configuration.VERSION_2_3_30);
    private Writer stringWriter = new StringWriter();



    private FreeMarkerPayloadManager(String jsonPayloads) {
        this.jsonPayloads = jsonPayloads;
        try {
            this.payloadTemplates = grm.getObjectMapper().readValue(this.jsonPayloads, new TypeReference<List<PayloadTemplate>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        initPayloads(jsonPayloads);
    }

    public static synchronized FreeMarkerPayloadManager getInstance(String jsonPayloads) throws IOException {
        if (instance == null) {
            instance = new FreeMarkerPayloadManager( jsonPayloads );
        }
        return instance;
    }

    private void initPayloads(String jsonPayloads) {
        try {
            this.jsonPayloads = jsonPayloads;
            this.payloadTemplates = grm.getObjectMapper().readValue(this.jsonPayloads, new TypeReference<List<PayloadTemplate>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public String getPayload(Map<String,String> payloadFilter, @Nullable Map<String, Object> payLoad) {
        if (payLoad == null) {
            Map<String, Object> payload1 = new HashMap();
            payLoad = payload1;
        }

        Map<String,Object> payloadTemplateMetadata = findPayloadMetaData(payloadFilter);
        String payloadNameFromFilter = payloadFilter.get("payloadName");
        if (payloadTemplateMetadata != null) {
            try {
                String payloadName = payloadTemplateMetadata.get("payloadName").toString();
                String payloadTemplate = fetchTemplate(payloadTemplateMetadata.get("relativePath").toString());
                StringTemplateLoader templateLoader = new StringTemplateLoader();
                templateLoader.putTemplate(payloadName, payloadTemplate);
                Configuration cfg = new Configuration( Configuration.VERSION_2_3_30);
                cfg.setTemplateLoader(templateLoader);
                Template template = cfg.getTemplate(payloadName, StandardCharsets.UTF_8.toString());
                Writer stringWriter = new StringWriter();
                template.process(payLoad, stringWriter);
                String result = stringWriter.toString();
                stringWriter.flush();
                return result;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                e.printStackTrace();
            }
        }
        else {
            logger.warn("Template not found for: {}", payloadNameFromFilter);
        }
        return null;
    }


//    public PayloadTemplate findPayload(String apiTitle, String operationId, @Nullable String payloadType) {
//
//        return this.payloadTemplates.stream()
//                .filter(payload -> payload.getApiTitle().equalsIgnoreCase(apiTitle)
//                        && payload.getOperationId().equalsIgnoreCase(operationId)
//                        && payload.getType().equalsIgnoreCase(validateType(payloadType)) )
//                .findFirst()
//                .orElse(null);
//    }

    public final String validateType(String payloadType){
        if(payloadType == null){
            payloadType = "default";
        }
        return payloadType;
    }


    private Map<String,Object> findPayloadMetaData(Map<String, String> filterMap){
        Filter filter = buildFilter(filterMap);
        List<Map<String, Object>> result = JsonPath.parse(this.jsonPayloads).read("$[?]", filter);
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    private Filter buildFilter(Map<String, String> filterMap) {
        Set<Map.Entry<String, String>> entrySet = filterMap.entrySet();
        int setStart = 1;
        Criteria criteria = null;
        for (Map.Entry<String, String> entry : entrySet) {
            if (setStart == 1) {
                criteria = Criteria.where(entry.getKey()).eq(entry.getValue());

            } else {
                criteria.and(entry.getKey()).eq(entry.getValue());
            }
            setStart++;
        }
        return Filter.filter(criteria);
    }

    public String getPayloadTemplateAsString(Map<String, Object> payloadMetaData){
        try {
            return grm.getObjectMapper().writeValueAsString(payloadMetaData.get("payload"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String fetchTemplate(String freeMarkerTemplateLocation){
        return FileUtils.getFileOnClassPathAsString(freeMarkerTemplateLocation);
    }
}
