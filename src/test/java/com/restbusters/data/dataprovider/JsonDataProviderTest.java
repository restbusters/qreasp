package com.restbusters.data.dataprovider;

import com.restbusters.util.common.FileUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author Ed Vayn on 6/11/21
 * @project qreasp
 */
public class JsonDataProviderTest {

    private String SINGLE_TEST_CASE_FILE = "payload/test_data/data_provider_sample.json";
    private String MULTIPLE_TEST_CASE_FILE = "payload/test_data/data_provider_parameters_tests.json";
    private String SINGLE_CASE_METHOD = "testJsonDataProviderWithSingleElem";
    private String MULTI_CASE_METHOD = "testJsonDataProviderWithList";
    private String jsonSingleTestCase;
    private String jsonMultipleTestCases;
    private JsonDataProviderHelper dataProviderHelper;
    private Map<String, Map<String, String>> json = new HashMap<>();
    private List<Object> dataSet = new ArrayList<>();

    //A Dataprovider must be initialized in a setUp method before tests are executed.
    @BeforeClass(alwaysRun = true)
    private void setUp() {
        jsonSingleTestCase = FileUtils.getFileOnClassPathAsString(SINGLE_TEST_CASE_FILE);
        jsonMultipleTestCases = FileUtils.getFileOnClassPathAsString(MULTIPLE_TEST_CASE_FILE);
        Map<String, String> singleMap = new HashMap<>();
        singleMap.put("json", jsonSingleTestCase);
        singleMap.put("rootKey", null);
        json.put(SINGLE_CASE_METHOD, singleMap);
        Map<String, String> multiTestCaseMap = new HashMap<>();
        multiTestCaseMap.put("json", jsonMultipleTestCases);
        multiTestCaseMap.put("rootKey", "tests");
        dataProviderHelper = JsonDataProviderHelper.getInstance();
        dataProviderHelper.addJsonDataForMethod(SINGLE_CASE_METHOD, singleMap);
        dataProviderHelper.addJsonDataForMethod(MULTI_CASE_METHOD, multiTestCaseMap);
    }

    @Test(description = "Test JsonDataProvider Single Test Case", dataProvider = "jsonDataProvider", dataProviderClass = JsonDataProvider.class, priority = 1)
    private void testJsonDataProviderWithSingleElem(Map<String, Object> tests) {
        List<String> expectedValues = Arrays.asList("test1");
        List parameters = (List) tests.get("parameters");
        Assert.assertTrue(dataProviderHelper.isDataSet());
        Assert.assertTrue(dataProviderHelper.getDataSet() instanceof List);
        Assert.assertTrue(expectedValues.contains(tests.get("testName")));
        Assert.assertTrue(parameters.size() > 0, "Parameters list size must be greater than zero");
    }

    @Test(description = "Test JsonDataProvider Multiple Test Cases", dataProvider = "jsonDataProvider", dataProviderClass = JsonDataProvider.class, priority = 2)
    private void testJsonDataProviderWithList(Map<String, Object> tests) {
        List<String> expectedNameValues = Arrays.asList("test1", "test2");
        List parameters = (List) tests.get("parameters");
        Assert.assertTrue(dataProviderHelper.isDataSet());
        Assert.assertTrue(dataProviderHelper.getDataSet() instanceof List);
        Assert.assertTrue(expectedNameValues.contains(tests.get("testName")));
        Assert.assertTrue(parameters.size() > 0, "Parameters list size must be greater than zero");
    }
}
