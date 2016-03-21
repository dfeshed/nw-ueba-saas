package fortscale.monitoring.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gils
 * 20/03/2016
 */
@DisallowConcurrentExecution
public class OSMonitoringJob implements Job{

    private static Logger logger = LoggerFactory.getLogger(OSMonitoringJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing OS Monitoring job..");
    }
}
