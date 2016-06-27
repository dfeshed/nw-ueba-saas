package fortscale.streaming.service.aggregation.feature.event;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "streaming.aggregation.service")
public class AggrKafkaEventTopologyServiceMetrics extends StatsMetricsGroup {
    public AggrKafkaEventTopologyServiceMetrics(StatsService statsService, String type, String name) {
        super(statsService, AggrKafkaEventTopologyService.class, new StatsMetricsGroupAttributes() {{
            addTag("type", type);
            addTag("name", name);
        }});
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long sent;
}
