package fortscale.services.metrics;

/**
 * Created by gaashh on 5/29/16.
 */


import fortscale.services.ipresolving.DhcpResolver;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for DhcpResolver
 */
@StatsMetricsGroupParams(name = "ETL.dhcp-resolver.service")
public class DhcpResolverMetrics extends StatsMetricsGroup {

    public DhcpResolverMetrics(StatsService statsService) {
        // Call parent ctor
        super(statsService, DhcpResolver.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {

                    }
                }
        );

    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long dhcpAlreadyInCache;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long addToCache;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updateCacheToNewHostname;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updateExpirationOnly;


    @StatsDoubleMetricParams(rateSeconds = 1)
    public long releaseHostname;
}


