package fortscale.streaming.service.model.metrics;

import fortscale.streaming.service.model.ModelBuildingRegistrationService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * General metrics for
 * @see ModelBuildingRegistrationService
 */
@StatsMetricsGroupParams(name = "streaming.model.commands")
public class ModelBuildingRegistrationServiceMetrics extends StatsMetricsGroup {
	public ModelBuildingRegistrationServiceMetrics(StatsService statsService) {
		super(statsService, ModelBuildingRegistrationService.class, new StatsMetricsGroupAttributes() {
			{
				// No tags to add
			}
		});
	}

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long processed;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long ignored;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long delete;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long store;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long nullRegistrations;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long pendingRegistrations;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long handledRegistrations;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long storeWithEarlierEndTime;
}
