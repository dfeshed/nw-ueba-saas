package fortscale.streaming.service.model.metrics;

import fortscale.streaming.service.model.ModelBuildingRegistrationService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
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
}
