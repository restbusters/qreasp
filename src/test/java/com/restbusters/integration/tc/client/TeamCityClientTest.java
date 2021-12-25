
package com.restbusters.integration.tc.client;

import com.jayway.jsonpath.JsonPath;
import com.restbusters.integraton.tc.client.TCBuildExecutor;
import com.restbusters.integraton.tc.client.TCHelper;
import com.restbusters.integraton.tc.client.TeamCityClient;
import com.restbusters.integraton.tc.client.model.post.job.PostBuild;
import com.restbusters.integraton.tc.client.model.task.BuildExecResult;
import com.restbusters.integraton.tc.client.model.task.BuildExecutorTask;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.util.common.RBFileUtils;
import com.restbusters.util.wiremock.WireMockManager;
import okhttp3.Response;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamCityClientTest {

    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private TeamCityClient tcClient;
    private String token;
    private String url;
    private GlobalResourceManager rc;
    private WireMockManager wireMockManager;
    private TCBuildExecutor tcBuildExecutor;

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
        String wireMockStubs = RBFileUtils.getFileOnClassPathAsString("wiremock/wiremock-stubs.json");
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
        Response response = tcClient.getBuildById("2750960");
        String body = response.body().string();
        logger.info(body);
        Assert.assertEquals("queued", JsonPath.read(body, "$.state"));
        logger.info(rc.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(body));
    }

    @Test(enabled = true)
    private void postBuild() throws Exception {
        Response response = tcClient.postBuild("requestBody");
        String body = response.body().string();
        logger.info(body);
        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals("queued", JsonPath.read(body, "$.state"));
        logger.info(rc.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(body));
    }

    @Test(description = "Check for Illegal argument exception", expectedExceptions = IllegalArgumentException.class)
    private void test_invalid_json() throws Exception {
        new TeamCityClient(null, token);
    }

    @Test()
    private void test_executor() throws Exception {
        PostBuild postBuild = TCHelper.buildTeamCityTriggerBuildRequest("testProject", "testBuildConfigId", null, null, null);
        BuildExecutorTask buildExecutorTask = new BuildExecutorTask();
        buildExecutorTask.setDescription("Test desc");
        List<PostBuild> postBuilds = new ArrayList<>();
        PostBuild postBuild2 = TCHelper.buildTeamCityTriggerBuildRequest("testProject2", "testBuildConfigId2", null, null, null);
        PostBuild postBuild3 = TCHelper.buildTeamCityTriggerBuildRequest("testProject3", "testBuildConfigId3", null, null, null);
        postBuilds.add(postBuild);
        postBuilds.add(postBuild2);
        postBuilds.add(postBuild3);
        buildExecutorTask.setPostBuild(postBuilds);
        buildExecutorTask.setMaxAttemptBuildCounter(10);
        buildExecutorTask.setMaxWaitTime(3000);
        this.tcBuildExecutor = new TCBuildExecutor(buildExecutorTask, this.tcClient);
        this.tcBuildExecutor.executeBuilds();
        BuildExecutorTask result = this.tcBuildExecutor.getBuildExecutorTask();
        Assert.assertNotNull(MapUtils.isNotEmpty(result.getBuildMetaData()));
        Assert.assertEquals(result.getBuildMetaData().size(), 3);
        logger.info(GlobalResourceManager.getInstance().getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(buildExecutorTask));
    }

}
