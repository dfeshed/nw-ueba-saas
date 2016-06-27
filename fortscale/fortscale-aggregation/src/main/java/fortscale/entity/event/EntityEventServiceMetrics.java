package fortscale.entity.event;


import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "streaming.entity-events.service")
public class EntityEventServiceMetrics extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     */
    public EntityEventServiceMetrics(StatsService statsService) {
        super(statsService, EntityEventService.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        // no tags needed
                    }
                }
        );
    }

    @StatsDoubleMetricParams (rateSeconds = 1)
    public long sendNewEntityEventAndUpdateStore;
    @StatsDoubleMetricParams (rateSeconds = 1)
    public long sendEntityEventsInTimeRange;
    @StatsDateMetricParams
    public long sendNewEntityEventsAndUpdateStoreEpoch;
}
