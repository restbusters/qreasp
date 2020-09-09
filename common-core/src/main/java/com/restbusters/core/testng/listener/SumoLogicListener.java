package com.restbusters.core.testng.listener;

import com.restbusters.core.config.GlobalConfig;
import com.restbusters.core.resource.GlobalResourceManager;
import com.restbusters.core.testng.model.TestCaseStatus;
import com.sumologic.client.SumoLogic;
import com.sumologic.client.model.LogMessage;
import com.sumologic.client.searchjob.model.GetMessagesForSearchJobResponse;
import com.sumologic.client.searchjob.model.GetSearchJobStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class SumoLogicListener implements ITestListener {

    private TestCaseStatus testCaseStatus;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private GlobalConfig globalConfig = GlobalResourceManager.getInstance().getGlobalConfig();
    private SumoLogic sumoClient =  GlobalResourceManager.getInstance().getSumoClient();

    public void onTestStart(ITestResult iTestResult) {
        logger.info("Test started:{}", iTestResult.getMethod().getMethodName());
        this.testCaseStatus = new TestCaseStatus();

    }


    public void onTestSuccess(ITestResult iTestResult) {
        //skip
    }

    public void onTestFailure(ITestResult iTestResult) {
        logger.info("Test failed {}", iTestResult.getTestContext().getHost());
        this.getErrors(iTestResult,"FAIL");
    }

    public void onTestSkipped(ITestResult iTestResult) {
        //skip
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        //skip
    }

    public void onStart(ITestContext iTestContext) {
        //skip
    }

    public void onFinish(ITestContext iTestContext) {
        logger.info("Test Finished: {}", iTestContext.getName());
    }

    private void getErrors(ITestResult iTestResult, String status){
        String searchJobId = sumoClient.createSearchJob(
                "_sourceCategory=*/hawaii/pc | json \"$.log.contextMap.x-b3-traceid\" as traceId | where traceId = \"1414f9d0230070192c8ed36c8f78dbc9\"",  // This query will return all messages
                "2020-01-27T13:10:00",    // between this start time and
                "2020-01-27T13:11:00",    // this end time, specified in ISO 8601 format
                "america/los_angeles",    // and assuming we are in California.
                "false");
        System.out.printf("Search job ID: '%s'\n", searchJobId);
        try {

            // The following block shows how to poll
            // the Sumo Logic service for updates on
            // the status of the search job. Once a
            // search job has been started, it will
            // run to completion in the Sumo Logic
            // service, during which time we can get
            // status updates, counts of messages and
            // records produced so far, and we can
            // actually already start getting messages
            // and records that have already been
            // produced.
            int messageCount = 0;
            int recordCount = 0;
            GetSearchJobStatusResponse getSearchJobStatusResponse = null;

            // We will loop until the search job status
            // is either "DONE GATHERING RESULTS" or
            // "CANCELLED".
            while (getSearchJobStatusResponse == null ||
                    (!getSearchJobStatusResponse.getState().equals("DONE GATHERING RESULTS") &&
                            !getSearchJobStatusResponse.getState().equals("CANCELLED"))) {

                // Sleep for a little bit, so we don't hammer
                // the Sumo Logic service.
                Thread.sleep(5000);

                // Get the latest search job status.
                getSearchJobStatusResponse = sumoClient.getSearchJobStatus(searchJobId);

                // Extract the message and record counts for
                // using them later down the road.
                messageCount = getSearchJobStatusResponse.getMessageCount();
                recordCount = getSearchJobStatusResponse.getRecordCount();

                // Tell the user what's going on. Class
                // GetSearchJobStatusResponse has a nice toString()
                // implementation that will show the status and
                // the message and record counts.
                System.out.printf(
                        "Search job ID: '%s', %s\n",
                        searchJobId,
                        getSearchJobStatusResponse);
            }

            // If the last search job status indicated
            // that the search job was "CANCELLED", we
            // can't get messages or records.
            if (getSearchJobStatusResponse.getState().equals("CANCELLED")) {
                System.out.println("Ugh. Search job was cancelled. Exiting...");
                return;
            }

            // Now we can get some messages. We know
            // from polling the search job status that
            // the search job is "DONE GATHERING RESULTS".
            // We don't necessarily have to wait until
            // that time, but to keep this example simple,
            // we will only get the messages produced by
            // the search job once it is actually done.
            // So let's get the first 10 messages.
            int messageOffset = 0;
            int messageLength = Math.min(messageCount, 10);
            GetMessagesForSearchJobResponse getMessagesForSearchJobResponse =
                    sumoClient.getMessagesForSearchJob(searchJobId, messageOffset, messageLength);
            System.out.printf(
                    "Messages for search job ID: '%s', %s\n",
                    searchJobId,
                    getMessagesForSearchJobResponse);

            // And now we print a summary of each of the
            // messages we got returned. Notice how class
            // LogMessage has accessors for the most common
            // fields, including the raw log line.
            List<LogMessage> messages = getMessagesForSearchJobResponse.getMessages();
            for (LogMessage message : messages) {
                System.out.printf("  %s, %s, %s, %s, %s\n",
                        message.getTime(),
                        message.getSourceHost(),
                        message.getSourceName(),
                        message.getSourceCategory(),
                        message.getLogLine());
            }

            // A search job can, depending on the query,
            // return not just messages, but also records.
            // Messages are the raw log lines collected,
            // with some additional meta data, such as the
            // parsed timestamps, and so forth.
            //
            // If the query also included an aggregation
            // operator, for example if the query is
            //
            //        * | count by _sourceCategory
            //
            // then the search job result will also include
            // a number of records. The records represent
            // the aggregated result, or in other words,
            // in this case, the number of times each
            // _sourceCategory has appeared across all the
            // messages in the time range. Records are
            // basically a table, much like what we would
            // expect to have returned from a SQL database.
            // Records can be accessed in pretty much the
            // same way as messages:
            //
            //        int recordOffset = 0;
            //        int recordLength = Math.min(recordCount, 10);
            //        GetRecordsForSearchJobResponse getRecordsForSearchJobResponse =
            //                 sumoClient.getRecordsForSearchJob(searchJobId, recordOffset, recordLength);
            //        System.out.printf(
            //                 "Records for search job ID: '%s', %s\n",
            //                 searchJobId,
            //                 getRecordsForSearchJobResponse);
            //         List<SearchJobRecord> records = getRecordsForSearchJobResponse.getRecords();
            //         for (SearchJobRecord record : records) {
            //             System.out.printf("  %s, %d\n",
            //                     record.stringField("_sourcecategory"),
            //                     record.intField("_count"));
            //         }

        } catch (Throwable t) {

            // Yikes. We has an error.
            t.printStackTrace();

        } finally {

            try {

                // We are done, so let's cancel the
                // search job. Note that it will otherwise
                // hang around the Sumo Logic service
                // until some session timeout kicks in.
                // Proactively cancelling a search job
                // when we are done with it proves that we
                // are a good citizen.
                this.sumoClient.cancelSearchJob(searchJobId);

            } catch (Throwable t) {
                System.out.printf("Error cancelling search job: '%s'", t.getMessage());
                t.printStackTrace();
            }
        }
    }


}
