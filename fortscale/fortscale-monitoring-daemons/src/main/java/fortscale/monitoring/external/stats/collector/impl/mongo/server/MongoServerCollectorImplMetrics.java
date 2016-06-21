package fortscale.monitoring.external.stats.collector.impl.mongo.server;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "monitoring.external.stats.mongo.server")
public class MongoServerCollectorImplMetrics extends StatsMetricsGroup {

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long UpdateFailures;

    /**
     * c'tor
     *
     * @param statsService stats service
     */
    public MongoServerCollectorImplMetrics(StatsService statsService) {
        super(statsService, MongoServerCollectorImplMetrics.class, new StatsMetricsGroupAttributes() {{
            setManualUpdateMode(true);
        }});

    }

}
