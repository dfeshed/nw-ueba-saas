package fortscale.ml.model.metrics;

import fortscale.ml.model.ModelBuilderManager;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
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

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long process;

	@StatsDateMetricParams
	public long currentEndTime;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long processWithNoPreviousEndTime;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long processWithPreviousEndTime;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long processWithNoContextSelector;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long contextIds;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long successes;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long failures;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long retrieverFailures;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long builderFailures;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long storeFailures;
}
