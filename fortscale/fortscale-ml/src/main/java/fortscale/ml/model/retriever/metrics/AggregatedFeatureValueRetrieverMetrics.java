package fortscale.ml.model.retriever.metrics;

import fortscale.ml.model.retriever.AggregatedFeatureValueRetriever;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for a specific
 * @see AggregatedFeatureValueRetriever
 */
@StatsMetricsGroupParams(name = "streaming.model.retriever.aggregated-feature-value")
public class AggregatedFeatureValueRetrieverMetrics extends StatsMetricsGroup {
	public AggregatedFeatureValueRetrieverMetrics(StatsService statsService, String aggregatedFeatureEventConfName) {
		super(statsService, AggregatedFeatureValueRetriever.class, new StatsMetricsGroupAttributes() {
			{
				addTag("confName", aggregatedFeatureEventConfName);
			}
		});
	}
}
