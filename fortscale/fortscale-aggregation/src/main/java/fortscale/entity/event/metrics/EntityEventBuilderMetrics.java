package fortscale.entity.event.metrics;

import fortscale.entity.event.EntityEventBuilder;
import fortscale.entity.event.EntityEventService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "streaming.entity-events.builder")
public class EntityEventBuilderMetrics extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param confName     - entityEvent confName
     */
    public EntityEventBuilderMetrics(StatsService statsService, String confName,String contextFields) {
        super(statsService, EntityEventBuilder.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        addTag("contextFields",contextFields);
                        addTag("confName", confName);
                    }
                }
        );
    }
    @StatsDoubleMetricParams (rateSeconds = 1)
    public long updateEntityEventData;

    @StatsDoubleMetricParams (rateSeconds = 1)
    public long sendEntityEvent;

    @StatsDateMetricParams
    public long sendEntityEventTime;

    @StatsDoubleMetricParams (rateSeconds = 1)
    public long stopSendingEntityEventDueTooFutureModifiedDate;

    @StatsDoubleMetricParams (rateSeconds = 1)
    public long storeEntityEventDataListSizeHigherThenPageSize;

    @StatsDoubleMetricParams (rateSeconds = 1)
    public long entityEventDataListSizeHigherThenZero;

    @StatsDoubleMetricParams (rateSeconds = 1)
    public long sendEntityEventsInTimeRange;

    @StatsDoubleMetricParams (rateSeconds = 1)
    public long nullEntityEventData;


}
