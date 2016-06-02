package fortscale.streaming.service.ipresolving;
/**
 // * Created by gaashh on 6/2/16.
 */

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.stats.metrics.StreamingStatsMetricsUtils;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for IpResolvingStreamTask
 * Note: StreamingTaskCommonMetrics provides the common stream task metrics
 */
@StatsMetricsGroupParams(name = "streaming.ip-resolving.service")
public class EventsIpResolvingServiceMetrics extends StatsMetricsGroup {

    public EventsIpResolvingServiceMetrics(StatsService statsService, StreamingTaskDataSourceConfigKey dataSourceConfigKey) {

        // Call parent ctor
        super(statsService, EventsIpResolvingService.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        // Add data source config key tags
                        StreamingStatsMetricsUtils.addTagsFromDataSourceConfig(this, dataSourceConfigKey);


                    }
                }
        );

    }

    // Number of enrich events attempts
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long enrichEventsAttempts;

    // Number of enriched events with missing missed
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long enrichedEventMissingFields;

    // Number of enriched events that should not be resolved
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long enrichedEventShouldNotResolved;

    // Number of enriched events that were resolved
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long enrichedEventResolved;

    // Number of enriched events that were not resolved
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long enrichedEventNotResolved;

    // Number of enriched events that were resolved and hostname was overridden
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long enrichedEventResolvedOverridden;

    // Number of enriched events resolved for host name
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long enrichedEventResolveForHostname;

    // Number of enriched events resolved for host name and host name was over written
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long enrichedEventResolveForHostnameAndOverridden;

    // Number of reserved IPs that matched the reserved range
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long resolveReservedIpMatched;

    // Number of events that should be filtered
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long shouldFilterEvent;

    // Number of events that were send to the output topic (note: counted at IpResolvingStreamTask)
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long sentEventMessages;

    // Number of events that were send to the output topic and failed (note: counted at IpResolvingStreamTask)
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long sentEventMessageFailures;
}

