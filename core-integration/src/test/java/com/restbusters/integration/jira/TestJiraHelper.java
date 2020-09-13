package com.restbusters.integration.jira;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.google.common.collect.Iterators;
import com.restbusters.core.config.GlobalConfig;
import com.restbusters.core.resource.GlobalResourceManager;
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
    private void setUp(){
        prop = GlobalResourceManager.getInstance().getGlobalConfig();
        try {
            jiraHelper.init(prop.jiraUrl(), prop.jiraUser(), prop.jiraPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(enabled = false)
    private void find_isse_by_id() throws Exception {
        Optional<Issue> optionalIssue = jiraHelper.findIssueByIssueId("PROJECT_KEY-13364");
        Assert.assertTrue(optionalIssue.get() != null);
    }

    @Test(enabled = true)
    private void jql_search() throws Exception {
        String search = "project ='PROJECT_KEY' and issueType=Bug";
        List<Issue> issueList = jiraHelper.searchJiraWithJQL(search, 50, 0);
        logger.info(String.valueOf(issueList));
    }

    @Test(enabled = false)
    private void create_issue() throws Exception {
        Optional<Issue> optionalIssue = jiraHelper.findIssueByIssueId("PROJECT_KEY-13364");
        List<String> comps = Arrays.asList("Automation");
        Long issueTypeId = new Long(10050);
        Long priorityId = new Long(3);
        BasicIssue basicIssue = jiraHelper.createIssue("PROJECT_KEY", "TEST1", "sasha matsaylo", comps, "summary", issueTypeId, priorityId);
        Assert.assertNotNull(basicIssue);
        logger.info(String.valueOf(basicIssue.getKey()));
    }

    @Test(enabled = false)
    private void create_issue_2() throws Exception {
        Optional<Issue> optionalIssue = jiraHelper.findIssueByIssueId("RU-3307");
        List<String> comps = Arrays.asList("Automation");
        List<String> affectedVersionNames = Arrays.asList("IS 10.0.3");
        //bug 10004 bug
        //action item 10050
        Long issueTypeId = new Long(10004);
        Long priorityId = new Long(3);
        IssueInput issueInput = buildIssueInput("PROJECT_KEY", "TEST1", "sasha matsaylo", comps, "summary", issueTypeId, priorityId, affectedVersionNames);
        BasicIssue basicIssue = jiraHelper.createIssue(issueInput);
        Assert.assertNotNull(basicIssue, "Failed to create jira");
    }

    private IssueInput buildIssueInput(String projectKey, String description, String assignee, Iterable<String> components, String summary,
                                       @Nonnull Long issueTypeId, @Nullable Long priorityId, Iterable<String> affectedVersionNames) {
        IssueInputBuilder builder = new IssueInputBuilder();
        builder.setProjectKey(projectKey)
                .setDescription(description)
                .setIssueTypeId(issueTypeId)
                .setSummary(summary);

        if (priorityId != null) {
            builder.setPriorityId(priorityId);
        }

        if (StringUtils.isNoneBlank(assignee))
            builder.setAssigneeName(assignee);
        if (Iterators.size(components.iterator()) > 0){
            builder.setComponentsNames(components);
        }
        if (Iterators.size(affectedVersionNames.iterator()) > 0){
            builder.setAffectedVersionsNames(affectedVersionNames);
        }
        return builder.build();
    }

}