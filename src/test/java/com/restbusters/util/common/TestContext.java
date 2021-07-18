package com.restbusters.util.common;

import com.restbusters.data.context.Context;
import com.restbusters.exception.ContextKeyIsBlank;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sasha Matsaylo on 7/13/21
 * @project qreasp
 */
public class TestContext {

    private Context context;

    @BeforeClass(alwaysRun = true)
    private void setUp(){
        this.context = new Context();
    }

    @Test
    private void setMap() throws ContextKeyIsBlank {
        Map<String,String> map = new HashMap<>();
        map.put("key", "value");
        context.setValue("map1", map);
        Assert.assertTrue(context.getValue("map1") instanceof Map);
        Map<String,String> result = (Map<String, String>) context.getValue("map1");
        Assert.assertEquals(result.get("key"), "value");

    }

}
