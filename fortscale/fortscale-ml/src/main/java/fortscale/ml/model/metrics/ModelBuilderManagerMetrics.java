package fortscale.ml.model.metrics;

import fortscale.ml.model.ModelBuilderManager;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for a specific
 * @see ModelBuilderManager
 */
@StatsMetricsGroupParams(name = "streaming.model.manager")
public class ModelBuilderManagerMetrics extends StatsMetricsGroup {
	public ModelBuilderManagerMetrics(StatsService statsService, String modelConfName) {
		super(statsService, ModelBuilderManager.class, new StatsMetricsGroupAttributes() {
			{
				addTag("confName", modelConfName);
			}
		});
	}
}
