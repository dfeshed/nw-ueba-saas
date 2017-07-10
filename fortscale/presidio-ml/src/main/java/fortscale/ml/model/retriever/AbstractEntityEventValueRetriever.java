package fortscale.ml.model.retriever;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.SMART.EntityEvent;
import fortscale.entity.event.*;
import fortscale.ml.model.ModelBuilderData;
import fortscale.ml.model.ModelBuilderData.NoDataReason;
import fortscale.ml.model.exceptions.InvalidEntityEventConfNameException;
import fortscale.ml.model.retriever.metrics.EntityEventValueRetrieverMetrics;
import fortscale.ml.model.selector.EntityEventContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.time.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Stream;

@Configurable(preConstruction = true)
public abstract class AbstractEntityEventValueRetriever extends AbstractDataRetriever {
	private static final Logger logger = Logger.getLogger(AbstractEntityEventValueRetriever.class);

	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private FactoryService<IContextSelector> contextSelectorFactoryService;
	@Autowired
	private StatsService statsService;

	private EntityEventConf entityEventConf;
	private JokerFunction jokerFunction;
	private EntityEventValueRetrieverMetrics metrics;

	public AbstractEntityEventValueRetriever(AbstractEntityEventValueRetrieverConf config, boolean isAccumulation) {
		super(config);
		String entityEventConfName = config.getEntityEventConfName();
		entityEventConf = entityEventConfService.getEntityEventConf(entityEventConfName);
		init(entityEventConfName, isAccumulation);
	}

	private void init(String entityEventConfName, boolean isAccumulation) {
		if (entityEventConf == null) {
			throw new InvalidEntityEventConfNameException(entityEventConfName);
		}
		jokerFunction = getJokerFunction();
		metrics = new EntityEventValueRetrieverMetrics(statsService, entityEventConfName, isAccumulation);
	}

	public AbstractEntityEventValueRetriever(AbstractEntityEventValueRetrieverConf config, EntityEventConf entityEventConf, boolean isAccumulation) {
		super(config);
		this.entityEventConf = entityEventConf;

		init(config.getEntityEventConfName(), isAccumulation);
	}

	@Override
	public ModelBuilderData retrieve(String contextId, Date endTime) {
		// If the retrieve is called for building a global model
		if (contextId == null) {
			return retrieveUsingContextIds(endTime);
		}

		metrics.retrieveWithContextId++;
		Stream<JokerEntityEventData> jokerEntityEventsData = readJokerEntityEventData(
				entityEventConf, contextId, getStartTime(endTime), endTime);
		GenericHistogram reductionHistogram = new GenericHistogram();
		final boolean[] noDataInDatabase = {true};

		jokerEntityEventsData.forEach(jokerEntityEventData -> {
			noDataInDatabase[0] = false;
			Map<String, JokerAggrEventData> jokerAggrEventDataMap = getJokerAggrEventDataMap(jokerEntityEventData);
			Double entityEventValue = jokerFunction.calculateEntityEventValue(jokerAggrEventDataMap);
			// TODO: Retriever functions should be iterated and executed here.
			reductionHistogram.add(entityEventValue, 1d);
		});

		if (reductionHistogram.getN() == 0) {
			if (noDataInDatabase[0]) {
				return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
			} else {
				return new ModelBuilderData(NoDataReason.ALL_DATA_FILTERED);
			}
		} else {
			return new ModelBuilderData(reductionHistogram);
		}
	}

	protected abstract Stream<JokerEntityEventData> readJokerEntityEventData(EntityEventConf entityEventConf,
																			 String contextId,
																			 Date startTime,
																			 Date endTime);

	private ModelBuilderData retrieveUsingContextIds(Date endTime) {
		metrics.retrieveWithNoContextId++;
		Date startTime = getStartTime(endTime);
		EntityEventContextSelectorConf conf = new EntityEventContextSelectorConf(entityEventConf.getName());
		IContextSelector contextSelector = contextSelectorFactoryService.getProduct(conf);
		Set<String> contextIds = contextSelector.getContexts(new TimeRange(startTime, endTime));
		metrics.contextIds++;
		logger.info("Number of contextIds: " + contextIds.size());
		GenericHistogram reductionHistogram = new GenericHistogram();

		if (contextIds.isEmpty()) {
			return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
		}

		for (String contextId : contextIds) {
			readJokerEntityEventData(entityEventConf, contextId, startTime, endTime)
					.mapToDouble(jokerEntityEventData -> {
						metrics.entityEventsData++;
						return jokerFunction.calculateEntityEventValue(getJokerAggrEventDataMap(jokerEntityEventData));
					})
					.max()
					.ifPresent(maxEntityEventValue -> {
						// TODO: Retriever functions should be iterated and executed here.
						reductionHistogram.add(maxEntityEventValue, 1d);
					});
		}

		if (reductionHistogram.getN() == 0) {
			return new ModelBuilderData(NoDataReason.ALL_DATA_FILTERED);
		} else {
			return new ModelBuilderData(reductionHistogram);
		}
	}

	@Override
	public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
		throw new UnsupportedOperationException(String.format(
				"%s does not support retrieval of a single feature",
				getClass().getSimpleName()));
	}

	@Override
	public String getContextId(Map<String, String> context) {
		metrics.getContextId++;
		Assert.notNull(context, "context cannot be null.");
		return EntityEventBuilder.getContextId(context);
	}

	private JokerFunction getJokerFunction() {
		String jokerFunctionJson = entityEventConf.getEntityEventFunction().toJSONString();

		try {
			return (new ObjectMapper()).readValue(jokerFunctionJson, JokerFunction.class);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to deserialize Joker function JSON %s.", jokerFunctionJson);
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}
	}

	private static Map<String, JokerAggrEventData> getJokerAggrEventDataMap(JokerEntityEventData jokerEntityEventData) {
		Map<String, JokerAggrEventData> jokerAggrEventDataMap = new HashMap<>();

		for (JokerAggrEventData jokerAggrEventData : jokerEntityEventData.getJokerAggrEventDatas()) {
			jokerAggrEventDataMap.put(
					jokerAggrEventData.getFullAggregatedFeatureEventName(),
					jokerAggrEventData
			);
		}

		return jokerAggrEventDataMap;
	}

	@Override
	public Set<String> getEventFeatureNames() {
		metrics.getEventFeatureNames++;
		Set<String> set = new HashSet<>(1);
		set.add(EntityEvent.ENTITY_EVENT_VALUE_FIELD_NAME);
		return set;
	}

	@Override
	public List<String> getContextFieldNames() {
		metrics.getContextFieldNames++;
		return entityEventConf.getContextFields();
	}
}
