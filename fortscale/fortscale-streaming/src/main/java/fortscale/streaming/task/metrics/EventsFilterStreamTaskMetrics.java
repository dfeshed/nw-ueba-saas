package fortscale.streaming.task.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for HDFSWriterStreamTask
 * Note: StreamingTaskCommonMetrics provides the common stream task metrics
 */
@StatsMetricsGroupParams(name = "streaming.events-filter.task")
public class EventsFilterStreamTaskMetrics extends StatsMetricsGroup {

	public EventsFilterStreamTaskMetrics(StatsService statsService) {
		// Call parent ctor
		super(statsService, EventsFilterStreamTaskMetrics.class,
				// Create anonymous attribute class with initializer block since it does not have ctor
				new StatsMetricsGroupAttributes() {
					{
						//addTag("foo", fooName);
					}
				}
		);

	}

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long vpnCloseMessages;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long vpnNonCloseMessages;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long filteredEvents;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long unfilteredEvents;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long sendMessageFailures;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long sentMessages;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long sourceIpInVpnAddressPool;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long cantExtractStateMessage;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long accountNameMatchesLoginAccountRegex;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long serviceNameMatchesLoginServiceRegex;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long serviceNameMatchesComputerName;
}

