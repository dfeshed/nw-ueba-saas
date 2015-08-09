package fortscale.streaming.service.aggregation.entity.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import java.util.*;

@Configurable(preConstruction = true)
public class EntityEventBuilder {
	private static final Logger logger = Logger.getLogger(EntityEventBuilder.class);
	private static final String CONTEXT_ID_SEPARATOR = "_";
	
	@Value("${streaming.event.field.type}")
    private String eventTypeFieldName;
    @Value("${streaming.event.field.type.entity_event}")
    private String eventTypeFieldValue;
    @Value("${streaming.entity_event.field.entity_event_type}")
    private String entityEventTypeFieldName;

	@Autowired
	private EntityEventDataStore entityEventDataStore;

	private long secondsToWaitBeforeFiring;
	private EntityEventConf entityEventConf;
	private JokerFunction jokerFunction;

	public EntityEventBuilder(long secondsToWaitBeforeFiring, EntityEventConf entityEventConf) {
		Assert.isTrue(secondsToWaitBeforeFiring >= 0);
		Assert.notNull(entityEventConf);

		this.secondsToWaitBeforeFiring = secondsToWaitBeforeFiring;
		this.entityEventConf = entityEventConf;
		String jokerFunctionJson = entityEventConf.getEntityEventFunction().toJSONString();
		try {
			this.jokerFunction = (new ObjectMapper()).readValue(jokerFunctionJson, JokerFunction.class);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to deserialize Joker function JSON %s", jokerFunctionJson);
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}
	}

	public void updateEntityEventData(AggrFeatureEventWrapper aggrFeatureEvent) {
		Assert.notNull(aggrFeatureEvent);
		EntityEventData entityEventData = getEntityEventData(aggrFeatureEvent);
		if (entityEventData != null && !entityEventData.isFired()) {
			entityEventData.addAggrFeatureEvent(aggrFeatureEvent);
			entityEventDataStore.storeEntityEventData(entityEventData);
		}
	}

	public void fireEntityEvents(long currentTimeInSeconds, String outputTopic, MessageCollector collector) {
		List<EntityEventData> listOfEntityEventData =
				entityEventDataStore.getEntityEventDataWithFiringTimeLteThatWereNotFired(entityEventConf.getName(), currentTimeInSeconds);
		for (EntityEventData entityEventData : listOfEntityEventData) {
			createAndSendEntityEvent(entityEventData, outputTopic, collector);
			entityEventData.setFired(true);
			entityEventDataStore.storeEntityEventData(entityEventData);
		}
	}

	private EntityEventData getEntityEventData(AggrFeatureEventWrapper aggrFeatureEvent) {
		List<String> contextFields = entityEventConf.getContextFields();
		Map<String, String> context = aggrFeatureEvent.getContext(contextFields);
		String contextId = getContextId(context);

		Long startTime = aggrFeatureEvent.getStartTime();
		Long endTime = aggrFeatureEvent.getEndTime();

		if (StringUtils.isBlank(contextId) || startTime == null || endTime == null) {
			return null;
		}

		EntityEventData entityEventData = entityEventDataStore.getEntityEventData(entityEventConf.getName(), contextId, startTime, endTime);
		if (entityEventData == null) {
			long firingTimeInSeconds = (System.currentTimeMillis() / 1000) + secondsToWaitBeforeFiring;
			entityEventData = new EntityEventData(firingTimeInSeconds, entityEventConf.getName(), context, contextId, startTime, endTime);
		}

		return entityEventData;
	}

	private String getContextId(Map<String, String> context) {
		List<Map.Entry<String, String>> listOfEntries = new ArrayList<>(context.entrySet());
		Collections.sort(listOfEntries, new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Map.Entry<String, String> entry1, Map.Entry<String, String> entry2) {
				return entry1.getKey().compareTo(entry2.getKey());
			}
		});

		List<String> listOfPairs = new ArrayList<>();
		for (Map.Entry<String, String> entry : listOfEntries) {
			listOfPairs.add(String.format("%s%s%s", entry.getKey(), CONTEXT_ID_SEPARATOR, entry.getValue()));
		}

		return StringUtils.join(listOfPairs, CONTEXT_ID_SEPARATOR);
	}

	private void createAndSendEntityEvent(EntityEventData entityEventData, String outputTopic, MessageCollector collector) {
		Map<String, AggrFeatureEventWrapper> aggrFeatureEventsMap = new HashMap<>();
		List<JSONObject> aggrFeatureEvents = new ArrayList<>();
		for (AggrFeatureEventWrapper aggrFeatureEvent : entityEventData.getAggrFeatureEvents()) {
			aggrFeatureEventsMap.put(
					String.format("%s.%s",
							aggrFeatureEvent.getBucketConfName(),
							aggrFeatureEvent.getAggregatedFeatureName()),
					aggrFeatureEvent);
			aggrFeatureEvents.add(aggrFeatureEvent.unwrap());
		}

		double entityEventValue = jokerFunction.calculateEntityEventValue(aggrFeatureEventsMap);

		JSONObject entityEvent = new JSONObject();
		entityEvent.put(eventTypeFieldName, eventTypeFieldValue);
		entityEvent.put(entityEventTypeFieldName, entityEventData.getEntityEventName());
		entityEvent.put("entity_event_value", entityEventValue);
		entityEvent.put("date_time_unix", entityEventData.getFiringTimeInSeconds());
		entityEvent.put("start_time_unix", entityEventData.getStartTime());
		entityEvent.put("end_time_unix", entityEventData.getEndTime());
		entityEvent.put("context", entityEventData.getContext());
		entityEvent.put("aggregated_feature_events", aggrFeatureEvents);

		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), entityEvent.toJSONString()));
	}
}
