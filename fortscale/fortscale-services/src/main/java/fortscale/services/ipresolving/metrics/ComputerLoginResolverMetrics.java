package fortscale.services.ipresolving.metrics;
/**
 * Created by gaashh on 6/2/16.
 */

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for ComputerLoginResolver
 */
@StatsMetricsGroupParams(name = "ip-resolving.computer-login-resolving")
public class ComputerLoginResolverMetrics extends StatsMetricsGroup {

	public ComputerLoginResolverMetrics(StatsService statsService, String nameTag) {

		// Call parent ctor
		super(statsService, ComputerLoginResolverMetrics.class,
				// Create anonymous attribute class with initializer block since it does not have ctor
				new StatsMetricsGroupAttributes() {
					{
						addTag("name", nameTag);
					}
				}
		);
	}

	// Number of checks for computer login
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long checkingIfComputerLoginNeedsUpdate;

	// Number of new or updated computer logins
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long computerLoginUpdated;

	// Number of times the computer login event repository was null
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long computerLoginEventRepositoryNull;

	// Number of times the computer login ip was in the blacklist and the ts in the time range
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long ipInBlackListAndTsInTimeRange;

	// Number of computer login events found in cache
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long foundComputerLoginEventInCache;

	// Number of computer login events found in the repository
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long computerLoginEventFoundInRepository;

	// Number of computer login ips added to black list
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long computerLoginIpAddedToBlackList;


	// Number of computer logins that weren't updated because there is already the same resolving for the same hour on the cache
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long computerLoginThatAlreadyAppeareInTheCache;
}

