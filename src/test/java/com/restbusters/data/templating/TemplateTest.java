package com.restbusters.data.templating;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.restbusters.exception.RecordNotFound;
import com.restbusters.resource.GlobalResourceManager;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;

/**
 * @author Sasha Matsaylo on 8/7/21
 * @project qreasp
 */
public class TemplateTest {

    private final String baseDir = "src/test/resources";
    private final String templateDir = "/payload/template/";
    private final String dataDir = "/payload/test_data/";
    private final String jsonSource = dataDir + "test_cases_exported.json";
    private final String jsonSource1 = dataDir + "test_pizza_ordering.json";
    private final String templateName = "jsonTestParamTemplate.ftl";
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String[] extension = { "ftl", "json" };
    private TemplateManager templateManager;

    @BeforeClass(alwaysRun = true)
    private void setUp() {
        this.templateManager = new TemplateManager("src/test/resources/payload/template/with-metadata", extension, true, ";", "=");
    }


    @Test(description = "Test Json Transfromation For Default template", priority = 1)
    private void testJsonTransformationForPrebuildTemplates() throws RecordNotFound, JsonProcessingException {

       //<#-- $name=sample2;description=sample2;version=02;inputFileName=sampleInput2.json$ -->
        String result = this.templateManager.processTemplateWithJsonInput("sample2", "02");
        Assert.assertNotNull(result);
        Object json = GlobalResourceManager.getInstance().getObjectMapper().readValue(result, Object.class);
        logger.info(GlobalResourceManager.getInstance().getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json));
    }

}
