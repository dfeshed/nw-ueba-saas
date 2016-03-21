package fortscale.monitoring.jobs;

import fortscale.monitoring.writer.MonitoringMetricsWriter;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @author gils
 * 20/03/2016
 */
@DisallowConcurrentExecution
public class MongoDBMonitoringJob implements Job {

    private static Logger logger = LoggerFactory.getLogger(MongoDBMonitoringJob.class);

    private MonitoringMetricsWriter monitoringMetricsWriter = new MonitoringMetricsWriter();

    private Random rand = new Random();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing MongoDB Monitoring job..");

        monitoringMetricsWriter.writeMetric("MongoDB CPU Usage " + rand.nextInt(100));
    }
}
