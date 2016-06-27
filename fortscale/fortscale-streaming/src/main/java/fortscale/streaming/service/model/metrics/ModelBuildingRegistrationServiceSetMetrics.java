package fortscale.streaming.service.model.metrics;

import fortscale.streaming.service.model.ModelBuildingRegistrationService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
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
}
