package fortscale.services.impl.metrics;

import fortscale.services.impl.UsernameService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for User Name Service
 */
@StatsMetricsGroupParams(name = "services.username.service")
public class UsernameServiceMetrics extends StatsMetricsGroup {

    public UsernameServiceMetrics(StatsService statsService) {

        super(statsService, UsernameService.class, new StatsMetricsGroupAttributes() {
            {
                //addTag("foo", fooName);
            }
        });
    }

    // Number of checks for username
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long lookingForUsername;

    // Number of user names that weren't found
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long usernameNotFound;

    // Number of checks for user id
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long lookingForUserId;

    // Number of user ids that weren't found
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long userIdNotFound;

    // Number of user names added to cache
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long usernameAddedToCache;

    // Number of checks for user named in cache
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long checkingUsernameInCache;

    // Number of checks for log user name
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long lookingForLogUsername;

    // Number of log user names that weren't found
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long logUsernameNotFound;

    // Number of user names updated in cache
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updateUsernameCache;

    // Number of log user names updated in cache
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updateLogUsernameCache;

    // Number of log user names added to cache
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long logUsernameAddedToCache;

    // Number of user names found by DN
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long getUsernameByDn;

    // Number of user names not found by DN
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long userNameNotFoundByDn;

    // Number of user names found by AD
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long getUsernameByAd;

    // Number of user names not found by DN
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long usernameNotFoundByAd;

    // Number of checks for normalized user named in cache
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long checkingNormalizedUsernameInCache;

    // Number of normalized user named added to cache
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long normalizedUsernameAddedToCache;
}