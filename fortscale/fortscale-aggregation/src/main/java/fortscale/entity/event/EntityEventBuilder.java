package fortscale.entity.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
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
	@Value("${impala.table.fields.epochtime}")
	private String epochtimeFieldName;

	private EntityEventDataStore entityEventDataStore;

	@Autowired
	private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;

	private long secondsToWaitBeforeFiring;
	private EntityEventConf entityEventConf;
	private JokerFunction jokerFunction;

	public EntityEventBuilder(long secondsToWaitBeforeFiring, EntityEventConf entityEventConf, EntityEventDataStore entityEventDataStore) {
		Assert.isTrue(secondsToWaitBeforeFiring >= 0);
		Assert.notNull(entityEventConf);
		Assert.notNull(entityEventDataStore);

		this.secondsToWaitBeforeFiring = secondsToWaitBeforeFiring;
		this.entityEventConf = entityEventConf;
		this.entityEventDataStore = entityEventDataStore;
		String jokerFunctionJson = entityEventConf.getEntityEventFunction().toJSONString();
		try {
			this.jokerFunction = (new ObjectMapper()).readValue(jokerFunctionJson, JokerFunction.class);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to deserialize Joker function JSON %s", jokerFunctionJson);
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}
	}

	public void updateEntityEventData(AggrEvent aggrFeatureEvent) {
		Assert.notNull(aggrFeatureEvent);
		EntityEventData entityEventData = getEntityEventData(aggrFeatureEvent);
		if (entityEventData != null) {
			entityEventData.addAggrFeatureEvent(aggrFeatureEvent);
			entityEventDataStore.storeEntityEventData(entityEventData);
		}
	}

	public void fireEntityEvents(long currentTimeInSeconds, String outputTopic, MessageCollector collector) {
		List<EntityEventData> listOfEntityEventData =
				entityEventDataStore.getEntityEventDataWithModifiedAtEpochtimeLteThatWereNotTransmitted(entityEventConf.getName(), currentTimeInSeconds - secondsToWaitBeforeFiring);
		for (EntityEventData entityEventData : listOfEntityEventData) {
			entityEventData.setTransmissionEpochtime(currentTimeInSeconds);
			entityEventData.setTransmitted(true);
			createAndSendEntityEvent(entityEventData, outputTopic, collector);
			entityEventDataStore.storeEntityEventData(entityEventData);
		}
	}

	private EntityEventData getEntityEventData(AggrEvent aggrFeatureEvent) {
		List<String> contextFields = entityEventConf.getContextFields();
		Map<String, String> context = aggrFeatureEvent.getContext(contextFields);
		String contextId = getContextId(context);

		Long startTime = aggrFeatureEvent.getStartTimeUnix();
		Long endTime = aggrFeatureEvent.getEndTimeUnix();

		if (StringUtils.isBlank(contextId) || startTime == null || endTime == null) {
			return null;
		}

		EntityEventData entityEventData = entityEventDataStore.getEntityEventData(entityEventConf.getName(), contextId, startTime, endTime);
		if (entityEventData == null) {
			entityEventData = new EntityEventData(entityEventConf.getName(), context, contextId, startTime, endTime);
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
		Map<String, AggrEvent> aggrFeatureEventsMap = new HashMap<>();
		List<JSONObject> aggrFeatureEvents = new ArrayList<>();
		for (AggrEvent aggrFeatureEvent : entityEventData.getIncludedAggrFeatureEvents()) {
			String aggrFeatureEventName = String.format("%s.%s", aggrFeatureEvent.getBucketConfName(), aggrFeatureEvent.getAggregatedFeatureName());
			aggrFeatureEventsMap.put(aggrFeatureEventName, aggrFeatureEvent);
			aggrFeatureEvents.add(aggrFeatureEventBuilderService.getAggrFeatureEventAsJsonObject(aggrFeatureEvent));
		}

		double entityEventValue = jokerFunction.calculateEntityEventValue(aggrFeatureEventsMap);

		JSONObject entityEvent = new JSONObject();
		entityEvent.put(eventTypeFieldName, eventTypeFieldValue);
		entityEvent.put(entityEventTypeFieldName, entityEventData.getEntityEventName());
		int tmp = (int) (entityEventValue*1000);
		double entityEventValue3DigitPrecision = tmp/1000d;
		entityEvent.put("entity_event_value", entityEventValue3DigitPrecision);
		entityEvent.put("creation_epochtime", entityEventData.getTransmissionEpochtime());
		entityEvent.put("start_time_unix", entityEventData.getStartTime());
		entityEvent.put("end_time_unix", entityEventData.getEndTime());
		// time of the event to be compared against other events from different types (raw events, entity event...)
		entityEvent.put(epochtimeFieldName, entityEventData.getEndTime());
		entityEvent.put("context", entityEventData.getContext());
		entityEvent.put("contextId", entityEventData.getContextId());
		entityEvent.put("aggregated_feature_events", aggrFeatureEvents);

		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), entityEvent.toJSONString()));
	}
}
