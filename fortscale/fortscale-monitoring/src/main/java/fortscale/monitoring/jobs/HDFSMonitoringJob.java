package fortscale.monitoring.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * HDFS monitoring job
 *
 * @author gils
 * 20/03/2016
 */
@DisallowConcurrentExecution
@Configurable(preConstruction = true)
public class HDFSMonitoringJob extends MonitoringJob{

    private static Logger logger = LoggerFactory.getLogger(HDFSMonitoringJob.class);

    private Random rand = new Random();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing HDFS Monitoring job..");

        super.execute(jobExecutionContext);
    }

    @Override
    public Map<String, Object> queryStats() {
        Map<String, Object> statisticsData = new HashMap<>();
        statisticsData.put("Data Source", "HDFS");
        statisticsData.put("CPU Usage", rand.nextInt(100));

        return statisticsData;
    }
}
