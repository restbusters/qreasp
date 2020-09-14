package com.restbusters.integration.stash;

import com.atlassian.stash.rest.client.api.StashClient;
import com.atlassian.stash.rest.client.api.entity.Branch;
import com.atlassian.stash.rest.client.api.entity.Page;
import com.atlassian.stash.rest.client.api.entity.Project;
import com.atlassian.stash.rest.client.api.entity.Repository;
import com.atlassian.stash.rest.client.httpclient.HttpClientConfig;
import com.atlassian.stash.rest.client.httpclient.HttpClientStashClientFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URI;

import java.util.List;
import java.util.Map;


public class TestStashApi {
    Logger log = LoggerFactory.getLogger(TestStashApi.class);

    @BeforeClass
    public void setup(){
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        String stashUsername = "<stash user name>";
        String stashPassword = "<stash password / token>";
        String stashUrl = "stash server url";
        try {
            HttpClientStashClientFactoryImpl stashClientFactory = new HttpClientStashClientFactoryImpl();
            StashClient stashClient = stashClientFactory.getStashClient(new HttpClientConfig(URI.create(stashUrl).toURL(), stashUsername, stashPassword));

            Page<Repository> repos = stashClient.getProjectRepositories("PO", 0, 30);
            System.out.println(repos.toString());
            Page<Project> projectPage = stashClient.getAccessibleProjects(0,10);
            List<Project> projects = projectPage.getValues();
            System.out.println(projects.get(0).getName());
            List<Repository> repoList = repos.getValues();


            Page<Branch> branchPage =
                    stashClient.getRepositoryBranches("PO", "hawaii-ui-automation",
                            null, 0, 10);

            // then
            List<Branch> branches = branchPage.getValues();

            //Map<String, Branch> branchMap = Maps.uniqueIndex(branches, STASH_BRANCH_ENTITY_TO_NAME::apply);

            // Get accessible projects and print them to console
            //Page<Project> projectPage = stashClient.getAccessibleProjects(0, 100);
            //projectPage.getValues().forEach(System.out::println);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test(enabled = true)
    public void getCommits(String repo){
        log.info("Response data:" + null);
    }
}