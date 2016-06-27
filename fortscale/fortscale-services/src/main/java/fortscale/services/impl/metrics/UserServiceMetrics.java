package fortscale.services.impl.metrics;

import fortscale.services.impl.UserServiceImpl;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for User Service
 */
@StatsMetricsGroupParams(name = "services.username.service")
public class UserServiceMetrics extends StatsMetricsGroup {

    public UserServiceMetrics(StatsService statsService) {

        super(statsService, UserServiceImpl.class, new StatsMetricsGroupAttributes() {
            {
                //addTag("foo", fooName);
            }
        });

    }

    // Number of created users
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long createdUsers;

	// Number of updated users
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long updatedUsers;

	// Number of times failed to update
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long failedToUpdate;

    // Number of empty username messages
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long emptyUsername;

	// Number of fails to create new user
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long failedToCreateUser;

	// Number of fails to find username
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long usernameNotFound;

	// Number of fails to find user id
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long userIdNotFound;

	// Number of times user id was found
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long userIdFound;

	// Number of times tags were found
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long tagsFound;

	// Number of fails to find user tags
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long tagsNotFound;

	// Number of times username was found
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long usernameFound;

	// Number of times thumbnail was found
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long thumbnailFound;

	// Number of times thumbnail was not found
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long thumbnailNotFound;

	// Number of empty guid messages
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long emptyGUID;

	// Number of empty dn messages
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long emptyDN;

	// Number of date parsing errors
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long dateParsingError;

	// Number of times failed to create deleted user
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long failedToCreateDeletedUser;

}