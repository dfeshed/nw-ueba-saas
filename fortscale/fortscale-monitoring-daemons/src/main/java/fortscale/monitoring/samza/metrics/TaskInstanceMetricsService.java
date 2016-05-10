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


    public TaskInstanceMetricsService(StatsService statsService, String task) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("task", task);
        this.metrics = new TaskInstanceMetrics(statsService, attributes);
    }
    public TaskInstanceMetricsService(StatsService statsService, String task,String topic) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("task", task);
        attributes.addTag("topic", topic);
        this.offsetsMetrics = new TaskInstanceOffsetsMetrics(statsService, attributes);
    }
}
