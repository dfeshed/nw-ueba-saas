package fortscale.streaming.task.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


/**
 * Metrics for VpnEnrichTask
 * Note: StreamingTaskCommonMetrics provides the common stream task metrics
 */
@StatsMetricsGroupParams(name = "streaming.vpn-enrich.task")
public class VpnEnrichTaskMetrics extends StatsMetricsGroup {


	public VpnEnrichTaskMetrics(StatsService statsService) {
		// Call parent ctor
		super(statsService, fortscale.streaming.task.metrics.VpnEnrichTaskMetrics.class,
				// Create anonymous attribute class with initializer block since it does not have ctor
				new StatsMetricsGroupAttributes() {
					{
						//addTag("foo", fooName);
					}
				}
		);

	}

	@StatsDateMetricParams
	public long timestampEpoch;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long filteredEvents;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long messageUserNameExtractionFailures;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long sendMessageFailures;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long badConfigs;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long unfilteredEvents;
}



