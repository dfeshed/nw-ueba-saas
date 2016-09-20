package fortscale.streaming.task.enrichment.metrics;

/**
 * Created by Amir Keren on 06/27/16.
 */

import fortscale.streaming.task.enrichment.UsernameNormalizationAndTaggingTask;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for UsernameNormalizationAndTaggingTask
 */
@StatsMetricsGroupParams(name = "streaming.username-normalization-and-tagging.task")
public class UsernameNormalizationAndTaggingTaskMetrics extends StatsMetricsGroup {

    public UsernameNormalizationAndTaggingTaskMetrics(StatsService statsService) {

        super(statsService, UsernameNormalizationAndTaggingTask.class, new StatsMetricsGroupAttributes() {});

    }

	// Number of messages parsed to JSON
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long parsedToJSONMessages;

	// Number of unknown configuration key messages
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long unknownConfigurationKeyMessages;

	// Number of unknown username normalization configuration messages
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long unknownNormalizationConfigurationMessages;

	// Number of messages with empty normalized username
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long emptyNormalizedUsernameMessages;

	// Number of messages that already have a normalized username
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long normalizedUsernameAlreadyExistsMessages;

	// Number of messages with empty username
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long emptyUsernameMessages;

	// Number of messages that were dropped
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long failedToNormalizeUsernameMessages;

	// Number of messages that failed to be forwarded to output topic
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long failedToForwardMessage;

}