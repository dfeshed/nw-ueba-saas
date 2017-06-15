package fortscale.ml.scorer.metrics;

import fortscale.ml.scorer.ScoringService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "streaming.scoring.dataSource")
public class ScoringServiceMetrics extends StatsMetricsGroup {
    /**
     * The c'tor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     */
    public ScoringServiceMetrics(StatsService statsService, String dataSource) {
        super(statsService, ScoringService.class, new StatsMetricsGroupAttributes() {
                    {
                        addTag("dataSource", dataSource);
                    }
                }
        );
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long dataSourceScorerNotFound;

    @StatsLongMetricParams
    public long dataSourceScorers;

    @StatsDateMetricParams
    public long calculateScoreTime;
}
