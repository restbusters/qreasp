package com.restbusters.core.util.common;

import ch.qos.logback.classic.Logger;
import org.skyscreamer.jsonassert.FieldComparisonFailure;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

public class TestGenericUtils {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Test
    private void testJsonComparison(){
        String json1 = "{}";
        String json2 = "{}";
        Map<String, List<FieldComparisonFailure>> result = GenericUtils.compareTwoJsonString(json1, json2, JSONCompareMode.STRICT);

    }

}
