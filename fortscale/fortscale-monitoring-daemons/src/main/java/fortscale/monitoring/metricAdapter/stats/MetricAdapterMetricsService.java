package fortscale.monitoring.metricAdapter.stats;

import fortscale.monitoring.samza.metrics.KafkaSystemConsumerMetrics;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;
import org.joda.time.DateTime;

import java.lang.reflect.Field;

/**
 * metric adapter stats monitoring counters
 */
public class MetricAdapterMetricsService {
    MetricAdapterMetrics metrics;

    public MetricAdapterMetrics getMetrics() {
        return metrics;
    }

    public MetricAdapterMetricsService(StatsService statsService,String jobName) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("process",jobName);
        this.metrics = new MetricAdapterMetrics(statsService, attributes);
    }


}
