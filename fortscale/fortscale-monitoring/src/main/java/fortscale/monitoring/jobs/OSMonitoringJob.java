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
 * @author gils
 * 20/03/2016
 */
@DisallowConcurrentExecution
@Configurable(preConstruction = true)
public class OSMonitoringJob implements Job{

    private static Logger logger = LoggerFactory.getLogger(OSMonitoringJob.class);

    @Autowired
    private MonitoringMetricsWriter monitoringMetricsWriter;

    private Random rand = new Random();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing OS Monitoring job..");

        monitoringMetricsWriter.writeMetric("OS CPU Usage " + rand.nextInt(100));
    }
}
