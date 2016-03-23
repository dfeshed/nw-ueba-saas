package fortscale.ml.model.retriever;

import fortscale.common.feature.Feature;
import fortscale.domain.core.EntityEvent;
import fortscale.entity.event.EntityEventBuilder;
import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.EntityEventMongoStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Configurable(preConstruction = true)
public class EntityEventScoreRetriever extends AbstractDataRetriever {
	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private EntityEventMongoStore entityEventMongoStore;

	private EntityEventScoreRetrieverConf config;
	private EntityEventConf entityEventConf;

	public EntityEventScoreRetriever(EntityEventScoreRetrieverConf config) {
		super(config);

		this.config = config;
		entityEventConf = entityEventConfService.getEntityEventConf(config.getEntityEventConfName());
		Assert.notNull(entityEventConf);
	}

	@Override
	public Map<Long, List<Double>> retrieve(String contextId, Date endTime) {
		Assert.isNull(contextId, this.getClass().getSimpleName() + " can't be used with a context");
		return entityEventMongoStore.getDateToTopEntityEvents(
				config.getEntityEventConfName(),
				endTime,
				config.getNumOfDays(),
				config.getNumOfAlertsPerDay()
		)
				.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						e -> e.getValue().stream().map(entity -> entity.getUnreduced_score()).collect(Collectors.toList()))
				);
	}

	@Override
	public Object retrieve(String contextId, Date endTime, Feature feature) {
		throw new UnsupportedOperationException(String.format(
				"%s does not support retrieval of a single feature",
				getClass().getSimpleName()));
	}

	@Override
	public String getContextId(Map<String, String> context) {
		return EntityEventBuilder.getContextId(context);
	}

	@Override
	public Set<String> getEventFeatureNames() {
		return Collections.singleton(EntityEvent.ENTITY_EVENT_UNREDUCED_SCORE_FIELD_NAME);
	}

	@Override
	public List<String> getContextFieldNames() {
		return entityEventConf.getContextFields().stream()
				.map(contextField -> String.format("%s.%s", EntityEvent.ENTITY_EVENT_CONTEXT_FIELD_NAME, contextField))
				.collect(Collectors.toList());
	}
}
