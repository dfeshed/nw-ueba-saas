package fortscale.entity.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.domain.core.EntityEvent;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
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

	public void sendNewEntityEventsAndUpdateStore(long currentTimeInSeconds, IEntityEventSender sender) {
		List<EntityEventData> listOfEntityEventData = entityEventDataStore
				.getEntityEventDataWithModifiedAtEpochtimeLteThatWereNotTransmitted(
				entityEventConf.getName(), currentTimeInSeconds - secondsToWaitBeforeFiring);
		for (EntityEventData entityEventData : listOfEntityEventData) {
			sendEntityEvent(entityEventData, currentTimeInSeconds, sender);
			entityEventDataStore.storeEntityEventData(entityEventData);
		}
	}

	public void sendEntityEventsInTimeRange(Date startTime, Date endTime, long currentTimeInSeconds, IEntityEventSender sender, boolean updateStore) {
		List<EntityEventData> listOfEntityEventData = entityEventDataStore
				.getEntityEventDataInTimeRange(entityEventConf.getName(), startTime, endTime);
		for (EntityEventData entityEventData : listOfEntityEventData) {
			sendEntityEvent(entityEventData, currentTimeInSeconds, sender);
			if (updateStore) {
				entityEventDataStore.storeEntityEventData(entityEventData);
			}
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

	private void sendEntityEvent(EntityEventData entityEventData, long currentTimeInSeconds, IEntityEventSender sender) {
		entityEventData.setTransmissionEpochtime(currentTimeInSeconds);
		entityEventData.setTransmitted(true);
		sender.send(createEntityEvent(entityEventData));
	}

	private JSONObject createEntityEvent(EntityEventData entityEventData) {
		/*
		 * NOTE: When not in a BDP run, the 'not included' set is empty,
		 * so adding it is redundant. But when in a BDP run, we want to
		 * create the entity event from all its aggregated feature events.
		 */
		Set<AggrEvent> allAggrFeatureEvents = new HashSet<>();
		allAggrFeatureEvents.addAll(entityEventData.getIncludedAggrFeatureEvents());
		allAggrFeatureEvents.addAll(entityEventData.getNotIncludedAggrFeatureEvents());

		Map<String, AggrEvent> aggrFeatureEventsMap = new HashMap<>();
		List<JSONObject> aggrFeatureEvents = new ArrayList<>();

		for (AggrEvent aggrFeatureEvent : allAggrFeatureEvents) {
			String aggrFeatureEventName = String.format("%s.%s", aggrFeatureEvent.getBucketConfName(), aggrFeatureEvent.getAggregatedFeatureName());
			aggrFeatureEventsMap.put(aggrFeatureEventName, aggrFeatureEvent);
			aggrFeatureEvents.add(aggrFeatureEventBuilderService.getAggrFeatureEventAsJsonObject(aggrFeatureEvent));
		}

		// Calculate the entity event (joker) value
		double entityEventValue = jokerFunction.calculateEntityEventValue(aggrFeatureEventsMap);

		JSONObject entityEvent = new JSONObject();
		entityEvent.put(eventTypeFieldName, eventTypeFieldValue);

		// Time of the event to be compared against other events from different types (raw events, entity events, etc.)
		entityEvent.put(epochtimeFieldName, entityEventData.getEndTime());
		entityEvent.put(entityEventTypeFieldName, entityEventData.getEntityEventName());

		entityEvent.put(EntityEvent.ENTITY_EVENT_NAME_FILED_NAME, entityEventData.getEntityEventName());
		entityEvent.put(EntityEvent.ENTITY_EVENT_VALUE_FILED_NAME, roundToEntityEventValuePrecision(entityEventValue));
		entityEvent.put(EntityEvent.ENTITY_EVENT_CREATION_EPOCHTIME_FILED_NAME, entityEventData.getTransmissionEpochtime());
		entityEvent.put(EntityEvent.ENTITY_EVENT_START_TIME_UNIX_FILED_NAME, entityEventData.getStartTime());
		entityEvent.put(EntityEvent.ENTITY_EVENT_END_TIME_UNIX_FILED_NAME, entityEventData.getEndTime());
		entityEvent.put(EntityEvent.ENTITY_EVENT_CONTEXT_FILED_NAME, entityEventData.getContext());
		entityEvent.put(EntityEvent.ENTITY_EVENT_CONTEXT_ID_FILED_NAME, entityEventData.getContextId());
		entityEvent.put(EntityEvent.ENTITY_EVENT_AGGREGATED_FEATURE_EVENTS_FILED_NAME, aggrFeatureEvents);

		return entityEvent;
	}

	private static double roundToEntityEventValuePrecision(double value) {
		return Math.round(value * 1000) / 1000d;
	}
}
