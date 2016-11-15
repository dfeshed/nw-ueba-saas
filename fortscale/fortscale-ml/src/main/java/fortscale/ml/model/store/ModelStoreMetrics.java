package fortscale.ml.model.store;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "streaming.model.model-store")
public class ModelStoreMetrics extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     */
    public ModelStoreMetrics(StatsService statsService) {
        super(statsService, ModelStore.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        // no tags needed
                    }
                }
        );
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long saveModel;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long getModelDaos;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long getModelDaosWithNoContext;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long removeModels;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long getCollectionName;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long ensureCollectionExists;

}
