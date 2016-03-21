package fortscale.monitoring.jobs;

import fortscale.monitoring.writer.MonitoringMetricsWriter;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Random;

/**
 * HDFS monitoring job
 *
 * @author gils
 * 20/03/2016
 */
@DisallowConcurrentExecution
@Configurable(preConstruction = true)
public class HDFSMonitoringJob implements Job{

    private static Logger logger = LoggerFactory.getLogger(HDFSMonitoringJob.class);

    @Autowired
    private MonitoringMetricsWriter monitoringMetricsWriter;

    private Random rand = new Random();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing HDFS Monitoring job..");

        monitoringMetricsWriter.writeMetric("HDFS CPU Usage " + rand.nextInt(100));
    }
}
