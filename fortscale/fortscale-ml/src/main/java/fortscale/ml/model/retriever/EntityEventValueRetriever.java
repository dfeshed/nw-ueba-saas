package fortscale.ml.model.retriever;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.entity.event.*;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
public class EntityEventValueRetriever extends AbstractDataRetriever {
	private static final Logger logger = Logger.getLogger(EntityEventValueRetriever.class);

	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private EntityEventDataReaderService entityEventDataReaderService;

	private EntityEventConf entityEventConf;

	public EntityEventValueRetriever(EntityEventValueRetrieverConf config) {
		super(config);

		String entityEventConfName = config.getEntityEventConfName();
		entityEventConf = entityEventConfService.getEntityEventConf(entityEventConfName);
		Assert.notNull(entityEventConf);
	}

	@Override
	public Object retrieve(String contextId, Date endTime) {
		long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
		long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;
		Date startTime = new Date(TimestampUtils.convertToMilliSeconds(startTimeInSeconds));

		List<EntityEventData> entityEventsData = entityEventDataReaderService
				.findEntityEventsDataByContextIdAndTimeRange(
				entityEventConf, contextId, startTime, endTime);
		JokerFunction jokerFunction = getJokerFunction();
		GenericHistogram reductionHistogram = new GenericHistogram();

		for (EntityEventData entityEventData : entityEventsData) {
			Date entityEventDataTime = new Date(
					TimestampUtils.convertToMilliSeconds(entityEventData.getStartTime()));
			Double entityEventValue = jokerFunction.calculateEntityEventValue(
					getAggrEventsMap(entityEventData));

			for (IDataRetrieverFunction function : functions) {
				entityEventValue = (Double)function.execute(
						entityEventValue, entityEventDataTime, endTime);
			}

			reductionHistogram.add(entityEventValue, 1d);
		}

		return reductionHistogram.getN() > 0 ? reductionHistogram : null;
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
			aggrEventsMap.put(aggrEvent.getFullAggregatedFeatureName(), aggrEvent);
		}

		for (AggrEvent aggrEvent : entityEventData.getNotIncludedAggrFeatureEvents()) {
			aggrEventsMap.put(aggrEvent.getFullAggregatedFeatureName(), aggrEvent);
		}

		return aggrEventsMap;
	}
}
