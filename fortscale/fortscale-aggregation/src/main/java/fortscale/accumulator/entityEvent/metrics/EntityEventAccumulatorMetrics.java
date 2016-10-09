package fortscale.accumulator.entityEvent.metrics;

import fortscale.accumulator.entityEvent.EntityEventAccumulator;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by barak_schuster on 10/9/16.
 */
@StatsMetricsGroupParams(name = "accumulator.aggregated-feature-events.accumulator")
public class EntityEventAccumulatorMetrics extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param featureName  - feature to monitor
     */
    public EntityEventAccumulatorMetrics(StatsService statsService, String featureName) {
        super(statsService, EntityEventAccumulator.class,
                new StatsMetricsGroupAttributes() {
                    {
                        addTag("feature",featureName);
                    }
                });
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long run;
    @StatsDateMetricParams
    public long fromTime;
    @StatsDateMetricParams
    public long toTime;
}
