package com.restbusters.data.templating;

import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * @author Sasha Matsaylo on 8/6/21
 * @project qreasp
 */
public class TemplateTest {

    @Test
    public void testBulkLoadIntoList(){
        String[] extension = { "ftl" };
        List<Map<String,String>> templates = TemplateLoaderHelper.bulkTemplateLoader("src/test/resources/payload/template/with-metadata", extension, true, ";", "=");
        Assert.assertTrue(templates.size() > 0);
    }
}
