package fortscale.ml.scorer.metrics;

import fortscale.ml.scorer.ScorersService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "streaming.scoring.dataSource")
public class ScorersServiceMetrics extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     */
    public ScorersServiceMetrics(StatsService statsService,String dataSource) {
        super(statsService, ScorersService.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {

                        addTag("dataSource",dataSource);
                    }
                }
        );
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long dataSourceScorerNotFound;

    // amount of scorers per data source. loaded once at ScorersService initiation
    @StatsLongMetricParams
    public long dataSourceScorers;


    @StatsDateMetricParams
    public long calculateScoreTime;


}
