package fortscale.streaming.task.enrichment.metrics;

/**
 * Created by gaashh on 5/29/16.
 */

import fortscale.streaming.task.enrichment.IpResolvingStreamTask;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for IpResolvingStreamTask
 * Note: StreamingTaskCommonMetrics provides the common stream task metrics
 */
@StatsMetricsGroupParams(name = "streaming.task.ip-resolving")
public class IpResolvingStreamTaskMetrics extends StatsMetricsGroup {

    public IpResolvingStreamTaskMetrics(StatsService statsService) {
        // Call parent ctor
        super(statsService, IpResolvingStreamTask.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        //addTag("xxx", yyy);
                    }
                }
        );

    }

    // Number of process() task function calls.
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long xx;



}

