package fortscale.streaming.task.metrics;

import fortscale.streaming.task.AggregationEventsStreamTask;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "streaming.aggregation-events.task")
public class AggregationEventsStreamTaskMetrics extends StatsMetricsGroup {
    public AggregationEventsStreamTaskMetrics(StatsService statsService) {
        super(statsService, AggregationEventsStreamTask.class, new StatsMetricsGroupAttributes());
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long processedMessages;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long skippedMessages;

    @StatsDateMetricParams(name="lastMessageTime")
    public long lastMessageEpoch;
}


