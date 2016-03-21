package fortscale.ml.model.retriever;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfUtilService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.EntityEvent;
import fortscale.entity.event.*;
import fortscale.ml.model.exceptions.InvalidEntityEventConfNameException;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.*;
import java.util.stream.Collectors;

@Configurable(preConstruction = true)
public class EntityEventValueRetriever extends AbstractDataRetriever {
	private static final Logger logger = Logger.getLogger(EntityEventValueRetriever.class);

	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private EntityEventDataReaderService entityEventDataReaderService;

	private EntityEventConf entityEventConf;
	private JokerFunction jokerFunction;
	public EntityEventValueRetriever(EntityEventValueRetrieverConf config) {
		super(config);
		String entityEventConfName = config.getEntityEventConfName();
		entityEventConf = entityEventConfService.getEntityEventConf(entityEventConfName);
		jokerFunction = getJokerFunction();
		validate(config);
	}
	private void validate(EntityEventValueRetrieverConf config)
	{
		if(entityEventConf == null)
			throw new InvalidEntityEventConfNameException(config.getEntityEventConfName());
	}

	@Override
	public Object retrieve(String contextId, Date endTime) {
		List<EntityEventData> entityEventsData = entityEventDataReaderService
				.findEntityEventsDataByContextIdAndTimeRange(
				entityEventConf, contextId, getStartTime(endTime), endTime);
		GenericHistogram reductionHistogram = new GenericHistogram();

		for (EntityEventData entityEventData : entityEventsData) {
			Double entityEventValue = jokerFunction.calculateEntityEventValue(
					getAggrEventsMap(entityEventData));
			// TODO: Retriever functions should be iterated and executed here.
			reductionHistogram.add(entityEventValue, 1d);
		}

		return reductionHistogram.getN() > 0 ? reductionHistogram : null;
	}

	@Override
	public Object retrieve(String contextId, Date endTime, Feature feature) {
		throw new UnsupportedOperationException(String.format(
				"%s does not support retrieval of a single feature",
				getClass().getSimpleName()));
	}

	@Override
	public String getContextId(Map<String, String> context) {
		Map<String, String> updatedContextWithoutPrecedingContextStringInKey = new HashMap<>();
		for(Map.Entry entry: context.entrySet()) {
			String newKey = entry.getKey().toString().substring(EntityEvent.ENTITY_EVENT_CONTEXT_FIELD_NAME.length()+1);
			updatedContextWithoutPrecedingContextStringInKey.put(newKey, entry.getValue().toString());
		}
		return EntityEventBuilder.getContextId(updatedContextWithoutPrecedingContextStringInKey);
	}

	private JokerFunction getJokerFunction() {
		String jokerFunctionJson = entityEventConf.getEntityEventFunction().toJSONString();

		try {
			return (new ObjectMapper()).readValue(jokerFunctionJson, JokerFunction.class);
		} catch (Exception e) {
			String errorMsg = String.format(
					"Failed to deserialize Joker function JSON %s.", jokerFunctionJson);
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}
	}

	private static Map<String, AggrEvent> getAggrEventsMap(EntityEventData entityEventData) {
		Map<String, AggrEvent> aggrEventsMap = new HashMap<>();

		for (AggrEvent aggrEvent : entityEventData.getIncludedAggrFeatureEvents()) {
			String fullAggregatedFeatureEventName = AggregatedFeatureEventsConfUtilService
					.buildFullAggregatedFeatureEventName(
					aggrEvent.getBucketConfName(), aggrEvent.getAggregatedFeatureName());
			aggrEventsMap.put(fullAggregatedFeatureEventName, aggrEvent);
		}

		for (AggrEvent aggrEvent : entityEventData.getNotIncludedAggrFeatureEvents()) {
			String fullAggregatedFeatureEventName = AggregatedFeatureEventsConfUtilService
					.buildFullAggregatedFeatureEventName(
					aggrEvent.getBucketConfName(), aggrEvent.getAggregatedFeatureName());
			aggrEventsMap.put(fullAggregatedFeatureEventName, aggrEvent);
		}

		return aggrEventsMap;
	}

	@Override
	public Set<String> getEventFeatureNames() {
		Set<String> set = new HashSet<>(1);
		set.add(EntityEvent.ENTITY_EVENT_VALUE_FIELD_NAME);
		return set;
	}

	@Override
	public List<String> getContextFieldNames() {
		return entityEventConf.getContextFields().stream()
				.map(contextField -> String.format("%s.%s", EntityEvent.ENTITY_EVENT_CONTEXT_FIELD_NAME, contextField))
				.collect(Collectors.toList());
	}
}
