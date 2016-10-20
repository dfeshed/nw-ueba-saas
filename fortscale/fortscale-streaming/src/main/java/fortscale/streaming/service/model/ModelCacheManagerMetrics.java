package fortscale.streaming.service.model;

import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * model's cache metrics
 */
@StatsMetricsGroupParams(name = "streaming.model.cache-service")
public class ModelCacheManagerMetrics extends StatsMetricsGroup {

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param store        - rocksdb store name
     * @param modelConf    - full model conf name, i.e. date_time_unix.normalized_username.ssh
     */
    public ModelCacheManagerMetrics(StatsService statsService, String store, String modelConf) {
        super(statsService, ModelsCacheService.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        addTag("store", store);
                        addTag("confName", modelConf);
                    }
                }
        );
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long modelEndTimeExpired;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long getModel;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long modelNotFoundInKeyValueStore;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long modelNotFoundForContextId;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long modelNotFoundInTimePeriod;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long modelDoesNotExist;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long lastUsageTimeSet;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long lazyCacheModelEndTimeOutDated;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long lazyCacheCanNotLoadModelsCacheInfo;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long discreteCacheWrongModelType;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long discreteCacheNullFeature;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long discreteCacheWrongFeatureValue;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long deleteFromCache;


}
