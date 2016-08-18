package fortscale.ml.model.retriever.metrics;

import fortscale.ml.model.retriever.EntityEventValueRetriever;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for a specific
 * @see EntityEventValueRetriever
 */
@StatsMetricsGroupParams(name = "streaming.model.retriever.entity-event-value")
public class EntityEventValueRetrieverMetrics extends StatsMetricsGroup {
	public EntityEventValueRetrieverMetrics(StatsService statsService, String entityEventConfName) {
		super(statsService, EntityEventValueRetriever.class, new StatsMetricsGroupAttributes() {
			{
				addTag("confName", entityEventConfName);
			}
		});
	}

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long retrieveWithContextId;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long retrieveWithNoContextId;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long contextIds;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long entityEventsData;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long getContextId;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long getEventFeatureNames;

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long getContextFieldNames;
}
