package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * Created by cloudera on 5/8/16.
 */
public class TaskInstanceMetricsService {
    TaskInstanceMetrics metrics;
    TaskInstanceOffsetsMetrics offsetsMetrics;

    public TaskInstanceMetrics getTaskInstanceMetrics() {
        return metrics;
    }
    public TaskInstanceOffsetsMetrics getTaskInstanceOffsetsMetrics() {
        return offsetsMetrics;
    }


    public TaskInstanceMetricsService(StatsService statsService, String job) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("job", job);
        this.metrics = new TaskInstanceMetrics(statsService, attributes);
    }
    public TaskInstanceMetricsService(StatsService statsService, String job,String topic) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("job", job);
        attributes.addTag("topic", topic);
        attributes.addTag("topic_job", String.format("%s__%s",topic,job));
        this.offsetsMetrics = new TaskInstanceOffsetsMetrics(statsService, attributes);
    }
}
