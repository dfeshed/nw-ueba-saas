package fortscale.streaming.service.vpn.metrics;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.ipresolving.EventsIpResolvingService;
import fortscale.streaming.stats.metrics.StreamingStatsMetricsUtils;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

public class VpnEnrichServiceMetrics extends StatsMetricsGroup {

	public long geoToIpResolvingFailures;
	public int failedEvents;
	public int eventsValidationFailures;
	public long closedSessionsForNonExistingOrFailedSessions;
	public int droppedClosedEvents;
	public int cleanedEvents;
	public int openSessions;
	public int sentEvidences;
	public int completedSessions;

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
}
