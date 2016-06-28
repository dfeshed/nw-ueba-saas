package fortscale.streaming.service.vpn.metrics;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.ipresolving.EventsIpResolvingService;
import fortscale.streaming.stats.metrics.StreamingStatsMetricsUtils;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for VpnEnrichService
 */
@StatsMetricsGroupParams(name = "streaming.vpn-enrich.service")
public class VpnEnrichServiceMetrics extends StatsMetricsGroup {

	public VpnEnrichServiceMetrics(StatsService statsService, StreamingTaskDataSourceConfigKey dataSourceConfigKey) {

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

	@StatsDateMetricParams()
	public long enrichMessageEpoch;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long geoToIpResolvingFailures;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public int failedEvents;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public int eventsValidationFailures;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long closedSessionsForNonExistingOrFailedSessions;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public int droppedClosedEvents;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public int cleanedEvents;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public int openSessions;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public int sentEvidences;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public int completedSessions;
}
