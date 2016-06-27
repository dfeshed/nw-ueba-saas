package fortscale.collection.metrics;

/**
 * Created by gaashh on 5/29/16.
 */

import fortscale.collection.jobs.event.process.EventProcessJob;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for HDFSWriterStreamTask
 * Note: StreamingTaskCommonMetrics provides the common stream task metrics
 */
@StatsMetricsGroupParams(name = "etl.record-to-bean-item.service")
public class RecordToBeanItemMetrics extends StatsMetricsGroup {

    public RecordToBeanItemMetrics(StatsService statsService, String name) {
        // Call parent ctor
        super(statsService, EventProcessJob.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        addTag("name", name);
                    }
                }
        );

    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long record;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long recordFailedBecauseEmpty;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long recordFailedBecausePropertyException;


}


