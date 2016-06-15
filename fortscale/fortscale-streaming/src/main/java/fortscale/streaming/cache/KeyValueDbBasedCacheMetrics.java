package fortscale.streaming.cache;

/**
 * Created by gaashh on 6/2/16.
 */

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for KeyValueDbBasedCache
 */
@StatsMetricsGroupParams(name = "streaming.key-value-db-based-cache")
public class KeyValueDbBasedCacheMetrics extends StatsMetricsGroup {

    public KeyValueDbBasedCacheMetrics(StatsService statsService, String nameTag) {

        // Call parent ctor
        super(statsService, KeyValueDbBasedCache.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {

                        addTag("name", nameTag);

                    }
                }
        );

    }

    // Number of get operation
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long get;

    // Number of get operation with not found result
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long getNotFound;

    // Number of put operation
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long put;

    // Number of remove operation
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long remove;


}

