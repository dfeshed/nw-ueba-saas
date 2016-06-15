package fortscale.streaming.task.enrichment.metrics;

/**
 * Created by gaashh on 5/29/16.
 */

import fortscale.streaming.task.enrichment.IpResolvingStreamTask;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for IpResolvingStreamTask
 * Note: StreamingTaskCommonMetrics provides the common stream task metrics
 */
@StatsMetricsGroupParams(name = "streaming.ip-resolving.task")
public class IpResolvingStreamTaskMetrics extends StatsMetricsGroup {

    public IpResolvingStreamTaskMetrics(StatsService statsService) {

        // Call parent ctor
        super(statsService, IpResolvingStreamTask.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        //addTag("foo", fooName);
                    }
                }
        );

    }

    // Number of VPN IP pool updates messages
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long vpnIpPoolUpdatesMessages;

    // Number of cache updates messages
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long cacheUpdatesMessages;

    // Number of event (regular) messages
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long eventMessages;

    // Number of enriched event (regular) messages
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long enrichedEventMessages;


    // Number of events that were send to the output topic
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long sentEventMessages;

    // Number of events that were send to the output topic and failed
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long sentEventMessageFailures;

    // Number of events that should be filtered
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long filteredEventMessages;

    // Number of event messages with unknown data source
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long unknownDataSourceEventMessages;

}

