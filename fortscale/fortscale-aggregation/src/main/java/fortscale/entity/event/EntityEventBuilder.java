package fortscale.entity.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.domain.core.EntityEvent;
import fortscale.entity.event.metrics.EntityEventBuilderMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Configurable(preConstruction = true)
public class EntityEventBuilder {
	private static final Logger logger = Logger.getLogger(EntityEventBuilder.class);
	private static final String CONTEXT_ID_SEPARATOR = "_";



	@Value("${fortscale.entity.event.retrieving.page.size}")
	private int retrievingPageSize;
	@Value("${fortscale.entity.event.store.page.size}")
	private int storePageSize;

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

	@Autowired
	private StatsService statsService;

	private EntityEventBuilderMetrics metrics;
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

	public EntityEventBuilderMetrics getMetrics()
	{
		if (metrics==null)
		{
			metrics = new EntityEventBuilderMetrics(statsService,entityEventConf.getName(),getContextFieldsAsString());
		}
		return metrics;
	}
	public void updateEntityEventData(AggrEvent aggrFeatureEvent) {
		Assert.notNull(aggrFeatureEvent);

		getMetrics().updateEntityEventData++;

		Pair<Boolean, EntityEventData> entityEventData = getEntityEventData(aggrFeatureEvent);
		if (entityEventData != null) {
			if ((aggrFeatureEvent.isOfTypeP() && aggrFeatureEvent.getAggregatedFeatureValue() > 0) ||
					(aggrFeatureEvent.isOfTypeF() && aggrFeatureEvent.getScore() > 0)) {
				entityEventData.getRight().addAggrFeatureEvent(aggrFeatureEvent);
				entityEventDataStore.storeEntityEventData(entityEventData.getRight());
			} else {
				getMetrics().zeroFeature++;
				if (entityEventData.getLeft()) {
					entityEventDataStore.storeEntityEventData(entityEventData.getRight());
				}
			}
		}
		else
		{
			getMetrics().nullEntityEventData++;
		}
	}

	public void sendNewEntityEventsAndUpdateStore(long currentTimeInSeconds, IEntityEventSender sender) throws TimeoutException {
		long modifiedAtLte = currentTimeInSeconds - secondsToWaitBeforeFiring;
		List<EntityEventMetaData> listOfEntityEventMetaData = Collections.emptyList();
		//no page request loop is being executed here since the transmitted value is being changed after sending the entity event.
		PageRequest pageRequest = new PageRequest(0, retrievingPageSize, Sort.Direction.ASC, EntityEventMetaData.END_TIME_FIELD);
		listOfEntityEventMetaData = entityEventDataStore.getEntityEventDataThatWereNotTransmittedOnlyIncludeIdentifyingData(entityEventConf.getName(), pageRequest);
		List<EntityEventData> entityEventDataList = new ArrayList<>();
		for (EntityEventMetaData entityEventMetaData : listOfEntityEventMetaData) {
			EntityEventData entityEventData = entityEventDataStore.getEntityEventData(entityEventMetaData.getEntityEventName(), entityEventMetaData.getContextId(), entityEventMetaData.getStartTime(), entityEventMetaData.getEndTime());
			if(entityEventData.getModifiedAtEpochtime() > modifiedAtLte){
				getMetrics().stopSendingEntityEventDueTooFutureModifiedDate++;
//				listOfEntityEventMetaData = Collections.emptyList();// to keep the time order we don't send any other entity event.
				break;
			}
			sendEntityEvent(entityEventData, currentTimeInSeconds, sender);
			entityEventDataList.add(entityEventData);
			if(entityEventDataList.size()>=storePageSize) {
				getMetrics().storeEntityEventDataListSizeHigherThenPageSize++;
				entityEventDataStore.storeEntityEventDataList(entityEventDataList);
				entityEventDataList = new ArrayList<>();
			}
		}

		if(entityEventDataList.size() > 0) {
			getMetrics().entityEventDataListSizeHigherThenZero++;
			entityEventDataStore.storeEntityEventDataList(entityEventDataList);
		}
	}

	public void sendEntityEventsInTimeRange(Date startTime, Date endTime, long currentTimeInSeconds,
											IEntityEventSender sender, boolean updateStore) throws TimeoutException {
		getMetrics().sendEntityEventsInTimeRange++;

		List<EntityEventData> listOfEntityEventData = entityEventDataStore
				.getEntityEventDataWithEndTimeInRange(entityEventConf.getName(), startTime, endTime);
		for (EntityEventData entityEventData : listOfEntityEventData) {
			sendEntityEvent(entityEventData, currentTimeInSeconds, sender);
			if (updateStore) {
				entityEventDataStore.storeEntityEventData(entityEventData);
			}
		}
	}

	private String getContextFieldsAsString()
	{
		List<String> contextFields = entityEventConf.getContextFields();

		return Arrays.toString(contextFields.toArray(new String[contextFields.size()]));
	}

	private Pair<Boolean, EntityEventData> getEntityEventData(AggrEvent aggrFeatureEvent) {
		List<String> contextFields = entityEventConf.getContextFields();
		Map<String, String> context = aggrFeatureEvent.getContext(contextFields);
		String contextId = getContextId(context);

		Long startTime = aggrFeatureEvent.getStartTimeUnix();
		Long endTime = aggrFeatureEvent.getEndTimeUnix();

		if(StringUtils.isBlank(contextId))
		{
			logger.warn("there is a blank contextId for entityEventConf={}, AggregatedFeatureName={} " ,entityEventConf.getName(),aggrFeatureEvent.getAggregatedFeatureName());
			return null;
		}

		String nullTimeMsg="aggrFeatureEvent {} is empty for entityEventConf={}, AggregatedFeatureName={}";
		if (startTime == null) {
			logger.warn(nullTimeMsg,"startTime",entityEventConf.getName(),aggrFeatureEvent.getAggregatedFeatureName());
			return null;
		}
		if (endTime == null)
		{
			logger.warn(nullTimeMsg,"endTime",entityEventConf.getName(),aggrFeatureEvent.getAggregatedFeatureName());
			return null;
		}

		EntityEventData entityEventData = entityEventDataStore.getEntityEventData(entityEventConf.getName(), contextId, startTime, endTime);
		boolean isCreated = false;
		if (entityEventData == null) {
			entityEventData = new EntityEventData(entityEventConf.getName(), context, contextId, startTime, endTime);
			isCreated = true;
		}

		return new ImmutablePair<>(isCreated, entityEventData);
	}

	public static String getContextId(Map<String, String> context) {
		return context.entrySet().stream()
				.sorted((entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()))
				.map(entry -> StringUtils.join(entry.getKey(), CONTEXT_ID_SEPARATOR, entry.getValue()))
				.collect(Collectors.joining(CONTEXT_ID_SEPARATOR));
	}

	private void sendEntityEvent(EntityEventData entityEventData, long currentTimeInSeconds, IEntityEventSender sender) throws TimeoutException{
		getMetrics().sendEntityEvent++;
		getMetrics().sendEntityEventTime=currentTimeInSeconds;

		entityEventData.setTransmissionEpochtime(currentTimeInSeconds);
		entityEventData.setTransmitted(true);
		sender.send(createEntityEvent(entityEventData));
	}

	private JSONObject createEntityEvent(EntityEventData entityEventData) {
		entityEventData.getIncludedAggrFeatureEvents().addAll(entityEventData.getNotIncludedAggrFeatureEvents());
		entityEventData.getNotIncludedAggrFeatureEvents().clear();

		Map<String, JokerAggrEventData> aggrFeatureEventsMap = new HashMap<>();
		List<JSONObject> aggrFeatureEvents = new ArrayList<>();

		for (AggrEvent aggrFeatureEvent : entityEventData.getIncludedAggrFeatureEvents()) {
			String aggrFeatureEventName = String.format("%s.%s", aggrFeatureEvent.getBucketConfName(), aggrFeatureEvent.getAggregatedFeatureName());
			aggrFeatureEventsMap.put(aggrFeatureEventName, new JokerAggrEventData(aggrFeatureEvent));
			aggrFeatureEvents.add(aggrFeatureEventBuilderService.getAggrFeatureEventAsJsonObject(aggrFeatureEvent));
		}

		// Calculate the entity event (joker) value
		double entityEventValue = jokerFunction.calculateEntityEventValue(aggrFeatureEventsMap);

		JSONObject entityEvent = new JSONObject();
		entityEvent.put(eventTypeFieldName, eventTypeFieldValue);

		// Time of the event to be compared against other events from different types (raw events, entity events, etc.)
		entityEvent.put(epochtimeFieldName, entityEventData.getEndTime());
		entityEvent.put(entityEventTypeFieldName, entityEventData.getEntityEventName());

		entityEvent.put(EntityEvent.ENTITY_EVENT_NAME_FIELD_NAME, entityEventData.getEntityEventName());
		entityEvent.put(EntityEvent.ENTITY_EVENT_VALUE_FIELD_NAME, roundToEntityEventValuePrecision(entityEventValue));
		entityEvent.put(EntityEvent.ENTITY_EVENT_CREATION_EPOCHTIME_FIELD_NAME, entityEventData.getTransmissionEpochtime());
		entityEvent.put(EntityEvent.ENTITY_EVENT_START_TIME_UNIX_FIELD_NAME, entityEventData.getStartTime());
		entityEvent.put(EntityEvent.ENTITY_EVENT_END_TIME_UNIX_FIELD_NAME, entityEventData.getEndTime());
		entityEvent.put(EntityEvent.ENTITY_EVENT_CONTEXT_FIELD_NAME, entityEventData.getContext());
		entityEvent.put(EntityEvent.ENTITY_EVENT_CONTEXT_ID_FIELD_NAME, entityEventData.getContextId());
		entityEvent.put(EntityEvent.ENTITY_EVENT_AGGREGATED_FEATURE_EVENTS_FIELD_NAME, aggrFeatureEvents);

		return entityEvent;
	}

	private static double roundToEntityEventValuePrecision(double value) {
		return Math.round(value * 10000000) / 10000000d;
	}
}
