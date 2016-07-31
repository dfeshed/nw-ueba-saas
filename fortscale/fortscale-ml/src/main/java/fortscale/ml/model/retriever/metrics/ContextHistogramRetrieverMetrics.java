package fortscale.ml.model.retriever.metrics;

import fortscale.ml.model.retriever.ContextHistogramRetriever;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
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

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long retrieveAllFeatureValues;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long retrieveSingleFeatureValue;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long getContextId;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long getEventFeatureNames;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long getContextFieldNames;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long featureBuckets;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long replacePattern;
}
