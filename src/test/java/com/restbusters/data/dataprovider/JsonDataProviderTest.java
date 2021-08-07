package com.restbusters.data.dataprovider;

import com.restbusters.util.common.RBFileUtils;
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
    private JsonDataProviderHelper dataProviderHelper = JsonDataProviderHelper.getInstance();
    private Map<String, Map<String, String>> json = new HashMap<>();
    private List<Object> dataSet = new ArrayList<>();

    //A Dataprovider must be initialized in a setUp method before tests are executed.
    @BeforeClass(alwaysRun = true)
    private void setUp() {
        jsonSingleTestCase = RBFileUtils.getFileOnClassPathAsString(SINGLE_TEST_CASE_FILE);
        jsonMultipleTestCases = RBFileUtils.getFileOnClassPathAsString(MULTIPLE_TEST_CASE_FILE);
        dataProviderHelper.addJsonDataForMethod(SINGLE_CASE_METHOD, jsonSingleTestCase, null);
        dataProviderHelper.addJsonDataForMethod(MULTI_CASE_METHOD, jsonMultipleTestCases, "tests");
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
