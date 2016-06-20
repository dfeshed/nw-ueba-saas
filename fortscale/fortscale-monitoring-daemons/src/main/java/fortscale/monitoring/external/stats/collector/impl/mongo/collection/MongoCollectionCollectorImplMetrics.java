package fortscale.monitoring.external.stats.collector.impl.mongo.collection;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "external.stats.collector.mongo.collection")
public class MongoCollectionCollectorImplMetrics extends StatsMetricsGroup {

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updateCollectionNameFailures;

    @StatsLongMetricParams
    public long updatedCollections;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long collectionUpdateFailures;

    /**
     * c'tor
     *
     * @param statsService stats service
     */
    public MongoCollectionCollectorImplMetrics(StatsService statsService) {
        super(statsService, MongoCollectionCollectorImplMetrics.class, new StatsMetricsGroupAttributes() {{
            setManualUpdateMode(true);
        }});

    }

}
