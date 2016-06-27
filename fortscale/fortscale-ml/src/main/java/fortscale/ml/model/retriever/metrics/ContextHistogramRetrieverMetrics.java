package fortscale.ml.model.retriever.metrics;

import fortscale.ml.model.retriever.ContextHistogramRetriever;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for a specific
 * @see ContextHistogramRetriever
 */
@StatsMetricsGroupParams(name = "streaming.model.retriever.feature-bucket")
public class ContextHistogramRetrieverMetrics extends StatsMetricsGroup {
	public ContextHistogramRetrieverMetrics(StatsService statsService, String featureBucketConfName, String featureName) {
		super(statsService, ContextHistogramRetriever.class, new StatsMetricsGroupAttributes() {
			{
				addTag("confName", featureBucketConfName);
				addTag("featureName", featureName);
			}
		});
	}
}
