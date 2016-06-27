package fortscale.streaming.service.aggregation.feature.event.metrics;

import fortscale.streaming.service.aggregation.feature.event.AggrInternalAndKafkaEventTopologyService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "streaming.aggregation.service")
public class AggrInternalAndKafkaEventTopologyServiceMetrics extends StatsMetricsGroup {
    public AggrInternalAndKafkaEventTopologyServiceMetrics(StatsService statsService) {
        super(statsService, AggrInternalAndKafkaEventTopologyService.class, new StatsMetricsGroupAttributes());
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long failed;
}
