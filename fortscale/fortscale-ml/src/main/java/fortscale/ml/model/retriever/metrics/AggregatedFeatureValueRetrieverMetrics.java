package fortscale.ml.model.retriever.metrics;

import fortscale.ml.model.retriever.AggregatedFeatureValueRetriever;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for a specific
 * @see AggregatedFeatureValueRetriever
 */
@StatsMetricsGroupParams(name = "streaming.model.retriever.aggregated-feature-value")
public class AggregatedFeatureValueRetrieverMetrics extends StatsMetricsGroup {
	public AggregatedFeatureValueRetrieverMetrics(StatsService statsService,
												  String aggregatedFeatureEventConfName,
												  boolean isAccumulation) {
		super(statsService, AggregatedFeatureValueRetriever.class, new StatsMetricsGroupAttributes() {
			{
				addTag("confName", aggregatedFeatureEventConfName);
				addTag("isAccumulation", String.valueOf(isAccumulation));
			}
		});
	}

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long retrieve;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long aggregatedFeatureValues;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long getContextId;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long getEventFeatureNames;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long getContextFieldNames;
}
