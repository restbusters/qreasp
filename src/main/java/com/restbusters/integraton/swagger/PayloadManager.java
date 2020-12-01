package com.restbusters.integraton.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.restbusters.integraton.swagger.model.PayloadTemplate;
import com.restbusters.resource.GlobalResourceManager;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sasha Matsaylo on 2020-11-30
 * @project qreasp
 */
public class PayloadManager {

    private static PayloadManager instance;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private String jsonPayloads;
    private List<PayloadTemplate> payloadTemplates;
    private GlobalResourceManager grm = GlobalResourceManager.getInstance();



    private PayloadManager(String jsonPayloads) {
        this.jsonPayloads = jsonPayloads;
        try {
            this.payloadTemplates = grm.getObjectMapper().readValue(this.jsonPayloads, new TypeReference<List<PayloadTemplate>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static synchronized PayloadManager getInstance(String jsonPayloads) throws IOException {
        if (instance == null) {
            instance = new PayloadManager( jsonPayloads );
        }

        return instance;
    }

    public String getPayload(String apiTitle, String payloadName, @Nullable String payloadType, @Nullable Map<String, Object> payLoad) {
        if (payLoad == null) {
            Map<String, Object> payload1 = new HashMap();
            payLoad = payload1;
        }

        PayloadTemplate payloadTemplate = findPayload(apiTitle, payloadName, payloadType);
        if (payloadTemplate != null) {

            try {
                StringTemplateLoader templateLoader = new StringTemplateLoader();
                templateLoader.putTemplate(payloadName, payloadTemplate.getPayload().replaceAll("\\\\", ""));
                Configuration cfg = new Configuration( Configuration.VERSION_2_3_21 );
                cfg.setTemplateLoader(templateLoader);
                Template template = cfg.getTemplate(payloadName, StandardCharsets.UTF_8.toString());
                Writer out = new StringWriter();
                template.process(payLoad, out);
                return out.toString();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                e.printStackTrace();
            }
            return null;
        }
        else {
            logger.warn("Template not found for {} {}", apiTitle, payloadName);
            return null;
        }
    }


    public PayloadTemplate findPayload(String apiTitle, String operationId, @Nullable String payloadType) {

        return this.payloadTemplates.stream()
                .filter(payload -> payload.getApiTitle().equalsIgnoreCase(apiTitle)
                        && payload.getOperationId().equalsIgnoreCase(operationId)
                        && payload.getType().equalsIgnoreCase(validateType(payloadType)) )
                .findFirst()
                .orElse(null);
    }

    public final String validateType(String payloadType){
        if(payloadType == null){
            payloadType = "default";
        }
        return payloadType;
    }


    private String freemarkerProcess(Map<String, Object> payLoad, String templateStr) {
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        String template = "content";
        stringLoader.putTemplate(template, templateStr);
        Configuration cfg = new Configuration();
        cfg.setTemplateLoader(stringLoader);
        try {
            Template templateCon = cfg.getTemplate(template, "UTF-8");
            StringWriter writer = new StringWriter();
            templateCon.process(payLoad, writer);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
