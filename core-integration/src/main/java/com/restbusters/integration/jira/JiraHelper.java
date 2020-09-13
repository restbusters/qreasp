package com.restbusters.integration.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.google.common.collect.Iterators;
import com.restbusters.core.exception.RecordNotFound;
import io.atlassian.util.concurrent.Promise;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Sasha matsaylo on 2020-09-10
 * @project qreasp
 */
public class JiraHelper {

    private static JiraHelper instance;
    private JiraRestClient jiraRestClient;
    private String url;
    private String user;
    private String password;
    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());



    private JiraHelper() {
    }

    public static synchronized JiraHelper getInstance() {
        if (instance == null) {
            instance = new JiraHelper();
        }
        return instance;
    }

    public void init(String url, String user, String password) throws Exception {
        this.url = url;
        this.user = user;
        this.password = password;
        this.jiraRestClient =  new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(new URI(this.url), this.user, this.password);

    }

   //start to move jira helper methods here
   public Optional<Issue> findIssueByIssueId(String issueKey){
       Promise issuePromise = jiraRestClient.getIssueClient().getIssue(issueKey);
       Issue issue = (Issue) issuePromise.claim();
       if(issue != null){
           return Optional.of(issue);
       }
       return Optional.empty();
   }

    public Optional<Iterable<Transition>> getIssueTransitions(String issueKey) throws RecordNotFound {
        Optional<Issue> optionalIssue = findIssueByIssueId(issueKey);
        if(!optionalIssue.isPresent()){
            throw new RecordNotFound("Issue not found: " + issueKey);
        }
        Promise promiseTransitions = jiraRestClient.getIssueClient().getTransitions(optionalIssue.get());
        Iterable<Transition> transitions = (Iterable<Transition>) promiseTransitions.claim();
        if(transitions instanceof Collection){
            return Optional.of(transitions);
        }
        return Optional.empty();

    }

    public BasicIssue createIssue(String projectKey, String description, String assignee, Iterable<String> components, String summary,
                                  @Nonnull Long issueTypeId, @Nullable Long priorityId) {
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

        final IssueInput issueInput = builder.build();

        try {
            return jiraRestClient.getIssueClient().createIssue(issueInput).get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn( "JIRA REST createIssue error: " + e.getMessage(), e);
            return null;
        }

    }

    public BasicIssue createIssue(IssueInput issueInput) {

        try {
            return jiraRestClient.getIssueClient().createIssue(issueInput).get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("JIRA REST createIssue error: " + e.getMessage(), e);
            return null;
        }
    }

    public Iterable<Version> getVersionListByProject(String projectName, String test){
        return jiraRestClient.getProjectClient().getProject(projectName).claim().getVersions();
    }


    public void transitIssue(String issueKey, String transitionState) throws RecordNotFound {
        Optional<Issue> optionalIssue = findIssueByIssueId(issueKey);
        if(!optionalIssue.isPresent()){
            throw new RecordNotFound("Issue not found: " + issueKey);
        }
        Optional<Iterable<Transition>> optionalTransitions = getIssueTransitions(issueKey);
        if(!optionalTransitions.isPresent()){
            throw new RecordNotFound("Failed to get transitions for: " + issueKey);
        }
        Optional<Integer> optionalTransitionId = getTransitionId(optionalTransitions.get(), transitionState);
        if(!optionalTransitionId.isPresent()){
            throw new RecordNotFound("No transition state available: " + transitionState);
        }
        TransitionInput transitionInput = new TransitionInput(optionalTransitionId.get());
        jiraRestClient.getIssueClient().transition(optionalIssue.get(),transitionInput).claim();

    }

    public Optional<Integer> getTransitionId(Iterable<Transition> transitions, String transitionState) {
        for (Transition transition : transitions) {
            System.out.println(transition.getName());

            if (transition.getName().trim().equalsIgnoreCase(transitionState)) {
                return Optional.of(transition.getId());
            }
        }
        return Optional.empty();
    }

    public List<Issue> searchJiraWithJQL(String jqlSearch, @Nullable int maxBatchSize, int startAt){
        List<Issue> totalResult = new ArrayList<>();
        SearchResult searchResult = jiraRestClient.getSearchClient().searchJql(jqlSearch, maxBatchSize, startAt, null).claim();
        if(searchResult.getTotal() > 0){
            int totalRetrieved = Iterators.size(searchResult.getIssues().iterator());
            totalResult.addAll((Collection<? extends Issue>) searchResult.getIssues());
            while (totalRetrieved < searchResult.getTotal()){
                searchResult = jiraRestClient.getSearchClient().searchJql(jqlSearch, maxBatchSize, totalRetrieved, null).claim();
                totalRetrieved += Iterators.size(searchResult.getIssues().iterator());
                totalResult.addAll((Collection<? extends Issue>) searchResult.getIssues());
            }
            return totalResult;
        }
        else {
            return totalResult;
        }

    }
}