package fortscale.accumulator.entityEvent.metrics;

import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Monitors CRUD operations of {@link AccumulatedEntityEventStore}
 * Created by barak_schuster on 10/8/16.
 */
@StatsMetricsGroupParams(name = "accumulator.entity-events.store")
public class AccumulatedEntityEventStoreMetrics extends StatsMetricsGroup {

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param featureName  - feature to monitor crud op's stats
     */
    public AccumulatedEntityEventStoreMetrics(StatsService statsService, String featureName) {
        super(statsService, AccumulatedEntityEventStore.class,
                new StatsMetricsGroupAttributes() {
                    {
                        addTag("feature",featureName);
                    }
                });
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long insert;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long insertFailure;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long createCollection;
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long createFailure;

}
