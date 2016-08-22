package fortscale.ml.model.retriever;

import fortscale.common.feature.Feature;
import fortscale.domain.core.EntityEvent;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.EntityEventMongoStore;
import fortscale.ml.model.retriever.metrics.EntityEventUnreducedScoreRetrieverMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Configurable(preConstruction = true)
public class EntityEventUnreducedScoreRetriever extends AbstractDataRetriever {
	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private EntityEventMongoStore entityEventMongoStore;
	@Autowired
	private StatsService statsService;

	private EntityEventUnreducedScoreRetrieverConf config;
	private EntityEventUnreducedScoreRetrieverMetrics metrics;

	public EntityEventUnreducedScoreRetriever(EntityEventUnreducedScoreRetrieverConf config) {
		super(config);
		this.config = config;
		Assert.notNull(entityEventConfService.getEntityEventConf(config.getEntityEventConfName()));
		metrics = new EntityEventUnreducedScoreRetrieverMetrics(statsService, config.getEntityEventConfName());
	}

	@Override
	public Map<Long, List<Double>> retrieve(String contextId, Date endTime) {
		metrics.retrieve++;
		Assert.isNull(contextId, String.format("%s can't be used with a context", this.getClass().getSimpleName()));
		Map<Long, List<EntityEvent>> dateToTopEntityEvents = entityEventMongoStore.getDateToTopEntityEvents(
				config.getEntityEventConfName(),
				endTime,
				config.getNumOfDays(),
				config.getNumOfAlertsPerDay());

		metrics.dates += dateToTopEntityEvents.size();
		dateToTopEntityEvents.values().forEach(list -> metrics.topEntityEvents += list.size());

		return dateToTopEntityEvents.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()
				.stream()
				.map(EntityEvent::getUnreduced_score)
				.filter(unreducedScore -> unreducedScore != null)
				.collect(Collectors.toList())));
	}

	@Override
	public Object retrieve(String contextId, Date endTime, Feature feature) {
		throw new UnsupportedOperationException(String.format(
				"%s does not support retrieval of a single feature",
				getClass().getSimpleName()));
	}

	@Override
	public Set<String> getEventFeatureNames() {
		metrics.getEventFeatureNames++;
		return Collections.singleton(EntityEvent.ENTITY_EVENT_UNREDUCED_SCORE_FIELD_NAME);
	}

	@Override
	public List<String> getContextFieldNames() {
		metrics.getContextFieldNames++;
		return Collections.emptyList();
	}

	@Override
	public String getContextId(Map<String, String> context) {
		metrics.getContextId++;
		return null;
	}
}
