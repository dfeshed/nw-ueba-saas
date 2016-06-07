package fortscale.ml.model.retriever;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfUtilService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.EntityEvent;
import fortscale.entity.event.*;
import fortscale.ml.model.exceptions.InvalidEntityEventConfNameException;
import fortscale.ml.model.selector.EntityEventContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.Assert;

import java.util.*;

@Configurable(preConstruction = true)
public class EntityEventValueRetriever extends AbstractDataRetriever {
	private static final Logger logger = Logger.getLogger(EntityEventValueRetriever.class);

	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private EntityEventDataReaderService entityEventDataReaderService;
	@Autowired
	private FactoryService<IContextSelector> contextSelectorFactoryService;

	private String entityEventConfName;
	private EntityEventConf entityEventConf;
	private JokerFunction jokerFunction;

	@Value("${entity.event.value.retriever.page.size:100000}")
	private int entityEventDataRetrieverPageSize;

	public EntityEventValueRetriever(EntityEventValueRetrieverConf config) {
		super(config);
		entityEventConfName = config.getEntityEventConfName();
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
		// If the retrieve is called for building a global model
		// then use the retrieve method that uses pagination & projection
		if(contextId==null) {
			return retrieveUsingContextIds(endTime);
		}
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

	public Object retrieveUsingPagination(Date endTime) {
		GenericHistogram reductionHistogram = new GenericHistogram();
		int pageNumber = 0;
		List<EntityEventData> entityEventsData = null;

		logger.info(String.format("====== starting to retrieve - first page of %s... ======", entityEventConf.getName()));
		while( (entityEventsData = entityEventDataReaderService.findEntityEventsDataByContextIdAndTimeRange(
				entityEventConf, getStartTime(endTime), endTime, new PageRequest(pageNumber++, entityEventDataRetrieverPageSize)))
				!= null && entityEventsData.size()>0 ) {
			logger.info(String.format("Page %d retrieved.", pageNumber-1));
			for (EntityEventData entityEventData : entityEventsData) {
				Double entityEventValue = jokerFunction.calculateEntityEventValue(
						getAggrEventsMap(entityEventData));
				// TODO: Retriever functions should be iterated and executed here.
				reductionHistogram.add(entityEventValue, 1d);
			}
		}
		logger.info("====== retrieve finished ======");
		return reductionHistogram.getN() > 0 ? reductionHistogram : null;
	}

	public Object retrieveUsingContextIds(Date endTime) {
		Date startTime = getStartTime(endTime);
		IContextSelector contextSelector = contextSelectorFactoryService.getProduct(new EntityEventContextSelectorConf(entityEventConfName));
		List<String> contextIds = contextSelector.getContexts(startTime, endTime);

		GenericHistogram reductionHistogram = new GenericHistogram();
		List<EntityEventData> entityEventsData = null;

		for(String contextId: contextIds) {

			entityEventsData = entityEventDataReaderService.findEntityEventsDataByContextIdAndTimeRange(
				entityEventConf, contextId, startTime, endTime);

			for (EntityEventData entityEventData : entityEventsData) {
				Double entityEventValue = jokerFunction.calculateEntityEventValue(
						getAggrEventsMap(entityEventData));
				// TODO: Retriever functions should be iterated and executed here.
				reductionHistogram.add(entityEventValue, 1d);
			}
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
		Assert.notNull(context);
		return EntityEventBuilder.getContextId(context);
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
		return entityEventConf.getContextFields();
	}
}
