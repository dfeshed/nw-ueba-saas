package fortscale.collection.jobs;

import fortscale.collection.NotifyJobFinishListener;
import fortscale.collection.SchedulerShutdownListener;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.quartz.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class EventsFromScoringTableToStreamingJob extends FortscaleJob {

    private static Logger logger = Logger.getLogger(EventsFromScoringTableToStreamingJob.class);

    private ClassPathXmlApplicationContext context;
    private Scheduler scheduler;

    private int hoursToRun;
    private String batchSizeInMinutes;
    private String securityDataSources;
    private DateTime startTime;

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
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
                startJob("ScoringToAggregation", securityDataSource, args.toArray(new String[args.size()]));
            }
            startTime = startTime.plusHours(1);
        }
    }

    @Override
    protected void runSteps() throws Exception {
        System.setProperty("org.quartz.properties", "resources/jobs/quartz.properties");
        context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context.xml");
        scheduler = (Scheduler)context.getBean("jobScheduler");
        scheduler.getListenerManager().addSchedulerListener(new SchedulerShutdownListener(scheduler, context));
        runJobs();
        scheduler.shutdown();
        context.close();
    }

    private void startJob(String jobName, String group, String... params) throws Exception {
        JobKey jobKey = new JobKey(jobName, group);
        // register job listener to close the scheduler after job completion
        NotifyJobFinishListener.FinishSignal monitor = NotifyJobFinishListener.waitOnJob(scheduler, jobKey);
        // check if job exists
        if (scheduler.checkExists(jobKey)) {
            // build job data map if given
            JobDataMap dataMap = new JobDataMap();
            if (params != null && params.length > 0) {
                for (String param : params) {
                    String[] entry = param.split("=", 2);
                    dataMap.put(entry[0], entry[1]);
                }
            }
            if (!dataMap.isEmpty())
                scheduler.triggerJob(jobKey, dataMap);
            else
                scheduler.triggerJob(jobKey);
            // wait for job completion
            monitor.doWait();
        } else {
            System.out.println(String.format("job %s %s does not exist", jobName, group));
        }
    }

}