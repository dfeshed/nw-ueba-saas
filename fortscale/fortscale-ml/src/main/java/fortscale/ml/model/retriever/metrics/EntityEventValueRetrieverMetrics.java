package fortscale.ml.model.retriever.metrics;

import fortscale.ml.model.retriever.EntityEventValueRetriever;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
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
}
