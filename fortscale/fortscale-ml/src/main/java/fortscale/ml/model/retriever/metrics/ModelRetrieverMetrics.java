package fortscale.ml.model.retriever.metrics;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.retriever.ModelRetriever;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "streaming.model.retriever.model")
public class ModelRetrieverMetrics extends StatsMetricsGroup {
	public ModelRetrieverMetrics(StatsService statsService, ModelConf modelConf) {
		super(statsService, ModelRetriever.class, new StatsMetricsGroupAttributes() {
			{
				addTag("confName", modelConf.getName());
			}
		});
	}

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long retrieveCalls;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long retrievedModels;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long getContextId;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long getContextFieldNames;
}
