
package com.restbusters.integration.tc.client;

import com.jayway.jsonpath.JsonPath;
import com.restbusters.integraton.tc.client.TeamCityClient;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.util.common.FileUtils;
import com.restbusters.util.wiremock.WireMockManager;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;

public class TeamCityClientTest {

    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private TeamCityClient tcClient;
    private String token;
    private String url;
    private GlobalResourceManager rc;
    private WireMockManager wireMockManager;

    @BeforeClass(alwaysRun = true)
    private void setUp() throws Exception {
        //this.url = System.getenv("TC_AUTH_URL");
        this.url = "http://localhost:8090";
        Assert.assertNotNull(url);
        //this.token = System.getenv("TC_AUTH_TOKEN");
        this.token = "dummyToken";
        Assert.assertNotNull(token);
        tcClient = new TeamCityClient(url, token);
        this.rc = GlobalResourceManager.getInstance();
        String wireMockStubs = FileUtils.getFileOnClassPathAsString("wiremock/wiremock-stubs.json");
        this.wireMockManager = WireMockManager.getInstance(wireMockStubs);
    }

    @AfterSuite
    private void tearDown(){
        this.wireMockManager.stopWireMock();
    }

    @Test(enabled = false)
    private void getBuilds() throws Exception {
        Response response = tcClient.getBuilds();
        logger.info(response.body().string());
    }

    @Test
    private void getBuildById() throws Exception {
        Response response = tcClient.getBuildById("2750959");
        String body = response.body().string();
        Assert.assertEquals("queued", JsonPath.read(body, "$.state"));
        logger.info(rc.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(body));
    }

    @Test(enabled = true)
    private void postBuild() throws Exception {
        Response response = tcClient.postBuild("requestBody");
        String body = response.body().string();
        Assert.assertEquals("queued", JsonPath.read(body, "$.state"));
        logger.info(rc.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(body));
    }

}
