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

	// Number of usernames already normalized
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long usernameAlreadyNormalized;

}