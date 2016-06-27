package fortscale.streaming.service.model.metrics;

import fortscale.streaming.service.model.ModelBuildingRegistrationService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Set specific metrics for
 * @see ModelBuildingRegistrationService
 */
@StatsMetricsGroupParams(name = "streaming.model.commands.set")
public class ModelBuildingRegistrationServiceSetMetrics extends StatsMetricsGroup {
	public ModelBuildingRegistrationServiceSetMetrics(StatsService statsService, String setName) {
		super(statsService, ModelBuildingRegistrationService.class, new StatsMetricsGroupAttributes() {
			{
				addTag("setName", setName);
			}
		});
	}

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long processed;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long delete;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long store;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long pendingRegistrations;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long handledRegistrations;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long storeWithEarlierEndTime;

	@StatsDateMetricParams
	public long lastStoredEndTime;

	@StatsDateMetricParams
	public long lastHandledEndTime;
}
