package fortscale.streaming.service.ipresolving;
/**
 * Created by gaashh on 6/2/16.
 */

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
@StatsMetricsGroupParams(name = "ip-resolving.ip-to-hostname-resolver")
public class IpToHostnameResolverMetrics extends StatsMetricsGroup {

    public IpToHostnameResolverMetrics(StatsService statsService, String nameTag) {

        // Call parent ctor
        super(statsService, IpToHostnameResolverMetrics.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {

                        addTag("name", nameTag);

                    }
                }
        );

    }

    // Number of resolution attempts
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long resolveAttempts;

    // Last enrich message epoch
    @StatsDateMetricParams
    public long resolveMessageEpoch;

    // Number of resolution of valid IP addresses
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long validIpAddressResolutions;

    // Number of resolution of valid IP addresses
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long fileProviderResolutions;

    // Number of resolution with host name field
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long standardResolutions;

    // Number of resolution with DNS
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long dnsResolutions;

    // Number of queue resolutions that failed
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long queueResolutionsFailures;

    // Number of hostname in AD
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long hostnameInAD;

    // Number of hostname in blacklist
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long hostnameInBlackList;

    // Number of event outside time frame
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long eventOutsideTimeFrame;

    // Number of updateRetentionTime() calls
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updateRetentionTime;

    // Number of updateRetentionTime() calls that yielded an update
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updateRetentionTimeUpdates;

    // Number of removeIpFromComputerLoginResolverCache()
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long removeIpFromComputerLoginResolverCache;


}

