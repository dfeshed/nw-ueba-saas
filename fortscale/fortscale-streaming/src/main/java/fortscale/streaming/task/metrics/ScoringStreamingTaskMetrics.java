package fortscale.streaming.task.metrics;

import fortscale.streaming.task.ScoringTask;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for ENTITY_EVENTS_SCORING, RAW_EVENTS_SCORING, AGGREGATED_FEATURE_EVENTS_SCORING
 */
@StatsMetricsGroupParams(name = "streaming.scoring.task")
public class ScoringStreamingTaskMetrics  extends StatsMetricsGroup {

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     */
    public ScoringStreamingTaskMetrics(StatsService statsService) {
        super(statsService, ScoringTask.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                    }
                }
        );
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long eventsWithoutTimestamp;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long filteredEvents;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long sentEvents;

    @StatsDateMetricParams
    public long eventsTime;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long calculateScores;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long refreshModelCache;
}

