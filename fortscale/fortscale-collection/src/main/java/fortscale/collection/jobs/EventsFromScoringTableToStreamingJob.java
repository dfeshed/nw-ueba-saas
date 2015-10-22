package fortscale.collection.jobs;

import fortscale.collection.BatchScheduler;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import org.joda.time.DateTime;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@DisallowConcurrentExecution
public class EventsFromScoringTableToStreamingJob extends FortscaleJob {

    private static Logger logger = Logger.getLogger(EventsFromScoringTableToStreamingJob.class);

    private BatchScheduler batch;
    private int hoursToRun;
    private String batchSizeInMinutes;
    private String securityDataSources;
    private DateTime startTime;

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        batch = new BatchScheduler();
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        hoursToRun = jobDataMapExtension.getJobDataMapIntValue(map, "hoursToRun");
        batchSizeInMinutes = jobDataMapExtension.getJobDataMapStringValue(map, "batchSizeInMinutes");
        securityDataSources = jobDataMapExtension.getJobDataMapStringValue(map, "securityDataSources");
        startTime = new DateTime(jobDataMapExtension.getJobDataMapLongValue(map, "startTime"));
    }

    @Override
    protected int getTotalNumOfSteps() {
        return 1;
    }

    @Override
    protected boolean shouldReportDataReceived() {
        return false;
    }

    private void runJobs() throws Exception {
        //run the forwarding job for every hour and for every data source
        for (int hour = 0; hour < hoursToRun; hour++) {
            DateTime endTime = startTime.plusHours(1).minusSeconds(1);
            String latestEventTime = "latestEventTime=" + (endTime.getMillis() / 1000);
            String deltaInSec = "deltaTimeInSec=3599";
            List<String> args = new ArrayList();
            args.add(latestEventTime);
            args.add(deltaInSec);
            args.add(batchSizeInMinutes);
            for (String securityDataSource: securityDataSources.split(",")) {
                batch.startJob("ScoringToAggregation", securityDataSource, args.toArray(new String[args.size()]));
            }
            startTime = startTime.plusHours(1);
        }
    }

    @Override
    protected void runSteps() throws Exception {
        try {
            BatchScheduler batch = new BatchScheduler();
            batch.loadScheduler();
            runJobs();
            batch.shutdown();
        } catch (Exception ex) {
            logger.error("error in scheduling collection jobs", ex);
        }
    }

}