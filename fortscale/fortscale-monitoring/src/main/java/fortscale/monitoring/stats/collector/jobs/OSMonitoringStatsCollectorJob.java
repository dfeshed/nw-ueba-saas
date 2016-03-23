package fortscale.monitoring.stats.collector.jobs;

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
 * OS monitoring job
 *
 * @author gils
 * 20/03/2016
 */
@DisallowConcurrentExecution
@Configurable(preConstruction = true)
public class OSMonitoringStatsCollectorJob extends MonitoringStatsCollectorJob {

    private static Logger logger = LoggerFactory.getLogger(OSMonitoringStatsCollectorJob.class);

    private Random rand = new Random();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing OS Monitoring job..");

        super.execute(jobExecutionContext);
    }

    @Override
    public Map<String, Object> queryStats() {
        Map<String, Object> statisticsData = new HashMap<>();
        statisticsData.put("Data Source", "Linux");
        statisticsData.put("CPU Usage", rand.nextInt(100));

        return statisticsData;
    }
}
