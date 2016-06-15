package fortscale.monitoring.external.stats.collector.impl.mongo.db;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "external.stats.collector.mongo.db")
public class MongoDBCollectorImplMetrics extends StatsMetricsGroup {

    @StatsDoubleMetricParams( rateSeconds = 1)
    public long UpdateFailures;

    /**
     * c'tor
     * @param statsService stats service
     */
    public MongoDBCollectorImplMetrics(StatsService statsService) {
        super(statsService, MongoDBCollectorImplMetrics.class, new StatsMetricsGroupAttributes() {{
            setManualUpdateMode(true);
        }});

    }

}
