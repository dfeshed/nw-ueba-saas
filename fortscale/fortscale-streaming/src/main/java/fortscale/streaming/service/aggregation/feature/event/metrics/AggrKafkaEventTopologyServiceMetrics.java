package fortscale.streaming.service.aggregation.feature.event.metrics;

import fortscale.streaming.service.aggregation.feature.event.AggrKafkaEventTopologyService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "streaming.aggregation.service.send")
public class AggrKafkaEventTopologyServiceMetrics extends StatsMetricsGroup {
    public AggrKafkaEventTopologyServiceMetrics(StatsService statsService) {
        super(statsService, AggrKafkaEventTopologyService.class, new StatsMetricsGroupAttributes());
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long nullEvents;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long noOutpuTopic;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long failedToSend;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long sent;
}
