package fortscale.ml.model.retriever;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfUtilService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.EntityEvent;
import fortscale.entity.event.*;
import fortscale.ml.model.exceptions.InvalidEntityEventConfNameException;
import fortscale.ml.model.retriever.metrics.EntityEventValueRetrieverMetrics;
import fortscale.ml.model.selector.EntityEventContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.*;

@Configurable(preConstruction = true)
public class EntityEventValueRetriever extends AbstractDataRetriever {
	private static final Logger logger = Logger.getLogger(EntityEventValueRetriever.class);

	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private FactoryService<IContextSelector> contextSelectorFactoryService;
	@Autowired
	private StatsService statsService;

	private EntityEventDataCachedReaderService entityEventDataCachedReaderService;
	private String entityEventConfName;
	private EntityEventConf entityEventConf;
	private JokerFunction jokerFunction;
	private EntityEventValueRetrieverMetrics metrics;

	public EntityEventValueRetriever(EntityEventValueRetrieverConf config,
									 EntityEventDataCachedReaderService entityEventDataCachedReaderService) {
		super(config);
		entityEventConfName = config.getEntityEventConfName();
		entityEventConf = entityEventConfService.getEntityEventConf(entityEventConfName);
		jokerFunction = getJokerFunction();
		metrics = new EntityEventValueRetrieverMetrics(statsService, entityEventConfName);
		validate(config);
		this.entityEventDataCachedReaderService = entityEventDataCachedReaderService;
	}
	private void validate(EntityEventValueRetrieverConf config)
	{
		if(entityEventConf == null)
			throw new InvalidEntityEventConfNameException(config.getEntityEventConfName());
	}

	@Override
	public Object retrieve(String contextId, Date endTime) {
		// If the retrieve is called for building a global model
		if(contextId==null) {
			return retrieveUsingContextIds(endTime);
		}
		List<JokerEntityEventData> jokerEntityEventsDatas = entityEventDataCachedReaderService
				.findEntityEventsJokerDataByContextIdAndTimeRange(
				entityEventConf, contextId, getStartTime(endTime), endTime);
		GenericHistogram reductionHistogram = new GenericHistogram();

		for (JokerEntityEventData jokerEntityEventData : jokerEntityEventsDatas) {
			Double entityEventValue = jokerFunction.calculateEntityEventValue(
					getJokerAggrEventDataMap(jokerEntityEventData));
			// TODO: Retriever functions should be iterated and executed here.
			reductionHistogram.add(entityEventValue, 1d);
		}

		return reductionHistogram.getN() > 0 ? reductionHistogram : null;
	}

	public Object retrieveUsingContextIds(Date endTime) {
		Date startTime = getStartTime(endTime);
		IContextSelector contextSelector = contextSelectorFactoryService.getProduct(new EntityEventContextSelectorConf(entityEventConfName));
		List<String> contextIds = contextSelector.getContexts(startTime, endTime);
		logger.info("Number of contextIds: "+contextIds.size());

		GenericHistogram reductionHistogram = new GenericHistogram();
		List<JokerEntityEventData> entityEventsData = null;

		for(String contextId: contextIds) {

			entityEventsData = entityEventDataCachedReaderService.findEntityEventsJokerDataByContextIdAndTimeRange(
				entityEventConf, contextId, startTime, endTime);

			for (JokerEntityEventData jokerEntityEventData : entityEventsData) {
				Double entityEventValue = jokerFunction.calculateEntityEventValue(
						getJokerAggrEventDataMap(jokerEntityEventData));
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

	private static Map<String, JokerAggrEventData> getJokerAggrEventDataMap(JokerEntityEventData jokerEntityEventData) {
		Map<String, JokerAggrEventData> jokerAggrEventDatasMap = new HashMap<>();

		for (JokerAggrEventData jokerAggrEventData : jokerEntityEventData.getJokerAggrEventDatas()) {
			String fullAggregatedFeatureEventName = AggregatedFeatureEventsConfUtilService
					.buildFullAggregatedFeatureEventName(
							jokerAggrEventData.getBucketConfName(), jokerAggrEventData.getAggregatedFeatureName());
			jokerAggrEventDatasMap.put(fullAggregatedFeatureEventName, jokerAggrEventData);
		}

		return jokerAggrEventDatasMap;
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
