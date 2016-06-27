package fortscale.ml.model.retriever.metrics;

import fortscale.ml.model.retriever.EntityEventUnreducedScoreRetriever;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for a specific
 * @see EntityEventUnreducedScoreRetriever
 */
@StatsMetricsGroupParams(name = "streaming.model.retriever.entity-event-score")
public class EntityEventUnreducedScoreRetrieverMetrics extends StatsMetricsGroup {
	public EntityEventUnreducedScoreRetrieverMetrics(StatsService statsService, String entityEventConfName) {
		super(statsService, EntityEventUnreducedScoreRetriever.class, new StatsMetricsGroupAttributes() {
			{
				addTag("entityEventConf", entityEventConfName);
			}
		});
	}
}
