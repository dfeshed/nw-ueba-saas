package fortscale.monitoring.jobs;

import fortscale.monitoring.writer.MonitoringMetricsWriter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Abstract monitor job to provide basic functionality of query statistics and persistence
 *
 * @author gils
 * 21/03/2016
 */
@Service
abstract class MonitoringJob implements Job {

    @Autowired
    protected MonitoringMetricsWriter monitoringMetricsWriter;

    public void execute(JobExecutionContext jobExecutionContext)throws JobExecutionException {
        Map<String, Object> statisticsData = queryStats();

        monitoringMetricsWriter.writeMetric("test");
    }

    abstract Map<String, Object> queryStats();
}