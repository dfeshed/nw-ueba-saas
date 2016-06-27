package fortscale.streaming.task.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


/**
 * Metrics for HDFSWriterStreamTask
 * Note: StreamingTaskCommonMetrics provides the common stream task metrics
 */
@StatsMetricsGroupParams(name = "streaming.events-filter.task")
public class VpnEnrichTaskMetrics extends StatsMetricsGroup {


	public int filteredEvents;
	public long messageUserNameExtractionFailures;
	public int sendMessageFailures;
	public int badConfigs;
	public int unfilteredEvents;

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

	//@StatsDoubleMetricParams(rateSeconds = 1)

}



