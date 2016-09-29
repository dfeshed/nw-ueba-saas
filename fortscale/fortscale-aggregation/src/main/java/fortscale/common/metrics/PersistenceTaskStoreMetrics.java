package fortscale.common.metrics;

import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "streaming.persistence.store")
public class PersistenceTaskStoreMetrics extends StatsMetricsGroup {

    /**
     * C'tor
     * @param collectionName the collection with CRUD ops to be monitored
     */
    public PersistenceTaskStoreMetrics(StatsService statsService, String collectionName) {
        super(statsService, AggregatedFeatureEventsMongoStore.class, new StatsMetricsGroupAttributes() {{
            addTag("collection", collectionName);
        }});
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long bulkWrites;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long bulkWriteDocumentCount;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long bulkWritesNotAcknowledged;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long bulkWritesErrors;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writes;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long reads;
}
