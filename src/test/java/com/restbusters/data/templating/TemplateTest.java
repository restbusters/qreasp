package com.restbusters.data.templating;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.restbusters.exception.RecordNotFound;
import com.restbusters.resource.GlobalResourceManager;
import freemarker.core.InvalidReferenceException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * @author Sasha Matsaylo on 8/7/21
 * @project qreasp
 */
public class TemplateTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String[] extension = { "ftl", "json" };
    private TemplateManager templateManager;

    @BeforeClass(alwaysRun = true)
    private void setUp() {
        this.templateManager = new TemplateManager("src/test/resources/payload/template/with-metadata", extension, true, ";", "=");
    }


    @Test(description = "Test Json Transfromation For Default template", priority = 1)
    private void testJsonTransformationForPrebuildTemplates() throws RecordNotFound, IOException, TemplateException {

       //<#-- $name=sample2;description=sample2;version=02;inputFileName=sampleInput2.json$ -->
        String result = this.templateManager.processTemplateWithJsonInput("sample3", "0.1");
        Assert.assertNotNull(result);
        Object json = GlobalResourceManager.getInstance().getObjectMapper().readValue(result, Object.class);
        logger.info(GlobalResourceManager.getInstance().getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json));
    }


    @Test(description = "Test Json Transfromation For Default template", priority = 1)
    private void test_JsonTransformation_with_provided_json() throws RecordNotFound, IOException, TemplateException {
        String name = "overwritten name";
        String jsonPayload = "{\"status\":\"fromInput\",\"photoUrls\":[\"url1\",\"url2\"],\"id\":1,\"name\":\"overwritten name\",\"category_name\":\"categoryName\",\"category_id\":12,\"tags\"" +
                ":[{\"id\":1,\"name\":\"name1\"},{\"id\":2,\"name\":\"name2\"}],\"whichStatus\":\"status2\",\"status2\":\"fromInput2\"}";
        String result = this.templateManager.processTemplateWithJsonInput("sample3", "0.1", jsonPayload.replaceAll("\\\\", ""));
        Assert.assertNotNull(result);
        Object json = GlobalResourceManager.getInstance().getObjectMapper().readValue(result, Object.class);
        Assert.assertTrue(result.contains(name));
        logger.info(GlobalResourceManager.getInstance().getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json));
    }

    @Test(description = "Check for template not found exception", priority = 3, expectedExceptions = RecordNotFound.class)
    private void test_record_not_found_json() throws RecordNotFound, IOException, TemplateException {
        String jsonPayload = "{}";
        this.templateManager.processTemplateWithJsonInput("blabla", "0.1", jsonPayload);
    }

    @Test(description = "Check for Illegal argument exception", priority = 4, expectedExceptions = IllegalArgumentException.class)
    private void test_empty_json() throws RecordNotFound, TemplateException, IOException {
        String jsonPayload = "";
        this.templateManager.processTemplateWithJsonInput("sample3", "0.1", jsonPayload);
    }

    @Test(description = "Check for Illegal argument exception", priority = 5, expectedExceptions = IllegalArgumentException.class)
    private void test_invalid_json() throws RecordNotFound, TemplateException, IOException {
        String jsonPayload = "-";
        this.templateManager.processTemplateWithJsonInput("sample3", "0.1", jsonPayload);
    }

    @Test(description = "Check for InvalidReferenceException", priority = 6, expectedExceptions = InvalidReferenceException.class)
    private void test_invalid_payload_input() throws TemplateException, RecordNotFound, IOException {
        String jsonPayload = "{\"status\":\"fromInput\"}";
        this.templateManager.processTemplateWithJsonInput("sample3", "0.1", jsonPayload);
    }


}
