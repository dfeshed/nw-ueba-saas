package fortscale.services.impl.metrics;

/**
 * Created by Amir Keren on 06/27/16.
 */

import fortscale.services.impl.UsernameNormalizer;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for Username Normalizer
 */
@StatsMetricsGroupParams(name = "services.username-normalizer.service")
public class UsernameNormalizerMetrics extends StatsMetricsGroup {

    public UsernameNormalizerMetrics(StatsService statsService) {

        super(statsService, UsernameNormalizer.class, new StatsMetricsGroupAttributes() {});

    }

    // Number of normalized username attempts
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long normalizeUsername;

	// Number of normalized username attempts
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long normalizeUsernameSSH;

	// Number of normalized username attempts
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long normalizeUsernameSEC;

	// Number of normalized username attempts
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long normalizeUsernameDN;

	// Number of normalized username attempts
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long normalizeUsernameAD;

	// Number of usernames already normalized
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long usernameAlreadyNormalized;

	// Number of users not exist
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long userDoesNotExist;

	// Number of times more than one sam account found
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long moreThanOneSAMAccountFound;

	// Number of users created or updated
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long updateOrCreateUser;

	// Number of users created or updated
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long updateOrCreateUserSSH;

	// Number of times no sam account found
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long noSAMAccountFound;

	// Number of times no user was found by dn
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long noUserFoundByDN;

	// Number of times no user was foudn by ad
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long noUserFoundByAD;

	// Number of times no sam account found
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long noSAMAccountFoundSSH;

	// Number of times one sam account found
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long oneSAMAccountFoundSSH;

	// Number of times more than one sam account found
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long moreThanOneSAMAccountFoundSSH;

	// Number of users that was not mattched the regexp or thier  matching was not exist at the AD
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long MissNormalizationUsingheRegExpSec;

	// Number of users that was Missed normalization at Sec normalizer
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long MissNormalizationSec;

}
