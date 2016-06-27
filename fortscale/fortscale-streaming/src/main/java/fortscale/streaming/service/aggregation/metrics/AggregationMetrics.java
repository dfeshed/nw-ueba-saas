package fortscale.streaming.service.aggregation.metrics;

import fortscale.streaming.task.AggregationEventsStreamTask;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "streaming.aggregation.service")
public class AggregationMetrics extends StatsMetricsGroup {
    public AggregationMetrics(StatsService statsService, String dataSource) {
        super(statsService, AggregationEventsStreamTask.class, new StatsMetricsGroupAttributes() {{
            addTag("dataSource", dataSource);
        }});
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long messagesWithoutTimestamp;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long missingFeatureBucketConfs;
}


