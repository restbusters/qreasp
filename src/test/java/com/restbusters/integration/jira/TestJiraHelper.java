package com.restbusters.integration.jira;

/**
 * @author Sasha Matsaylo on 2020-09-23
 * @project qreasp
 */


import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.google.common.collect.Iterators;
import com.restbusters.config.GlobalConfig;
import com.restbusters.integraton.jira.JiraHelper;
import com.restbusters.resource.GlobalResourceManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author sasha on 2020-09-10
 * @project qreasp
 */
public class TestJiraHelper {

    private JiraHelper jiraHelper = JiraHelper.getInstance();
    private GlobalConfig prop;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @BeforeClass
    private void setUp() {
        prop = GlobalResourceManager.getInstance().getGlobalConfig();
        try {
            jiraHelper.init(prop.jiraUrl(), prop.jiraUser(), prop.jiraPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test(enabled = false)
    private void test_get_project() {
        //Optional project = this.jiraHelper.getProjectByProjectKey("ss");
        String user = this.jiraHelper.getProjectLead("sss").get();
        jiraHelper.getProjectVersionByName("ss", "ss");
        System.out.println("");
    }

    @Test(enabled = false)
    private void create_issue() throws Exception {
        List<String> comps = Arrays.asList("auto");
        Long issueTypeId = new Long(10004);
        Long priorityId = new Long(3);
        List<String> versions = Arrays.asList("versionName");
        BasicIssue basicIssue = jiraHelper.createIssue("project", "TEST1", "sasha matsaylo", comps, "summary", issueTypeId, priorityId, versions);
        Assert.assertNotNull(basicIssue);
        logger.info(String.valueOf(basicIssue.getKey()));
    }

}
