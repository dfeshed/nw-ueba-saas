package fortscale.monitoring.stats.collector.jobs;

import fortscale.monitoring.stats.collector.writer.MonitoringStatsCollectorMetricsWriter;
import net.minidev.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Abstract monitor statistics collector job to provide basic functionality of query and persistence
 *
 * @author gils
 * 21/03/2016
 */
@Service
abstract class MonitoringStatsCollectorJob implements Job {

    @Autowired
    protected MonitoringStatsCollectorMetricsWriter monitoringMetricsWriter;

    /**
     * The default behavior of monitoring statistics collector:
     * 1. Query statistics
     * 2. Create metrics json object
     * 2. Write metrics
     *
     * @param jobExecutionContext job execution context
     * @throws JobExecutionException job execution exception
     */
    public void execute(JobExecutionContext jobExecutionContext)throws JobExecutionException {
        Map<String, Object> statisticsData = queryStats();

        String metricsJsonValue = createMetricsJson(statisticsData);

        monitoringMetricsWriter.writeMetric(metricsJsonValue);
    }

    private String createMetricsJson(Map<String, Object> statisticsData) {
        JSONObject jsonObject = new JSONObject(statisticsData);
        return jsonObject.toJSONString();
    }

    abstract Map<String, Object> queryStats();
}