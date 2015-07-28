package fortscale.streaming.service.aggregation.entity.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.*;

@Configurable(preConstruction = true)
public class EntityEventBuilder {
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
			// TODO
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
		List<EntityEventData> listOfEntityEventData = entityEventDataStore.getEntityEventData(entityEventConf.getName(), currentTimeInSeconds);
		for (EntityEventData entityEventData : listOfEntityEventData) {
			createEntityEvent(entityEventData, outputTopic, collector);
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
			listOfPairs.add(String.format("%s = %s", entry.getKey(), entry.getValue()));
		}

		return StringUtils.join(listOfPairs, ", ");
	}

	private void createEntityEvent(EntityEventData entityEventData, String outputTopic, MessageCollector collector) {
		Map<String, AggrFeatureEventWrapper> aggrFeatureEventsMap = new HashMap<>();
		List<JSONObject> aggrFeatureEvents = new ArrayList<>();
		for (AggrFeatureEventWrapper aggrFeatureEvent : entityEventData.getAggrFeatureEvents()) {
			aggrFeatureEventsMap.put(
					String.format("%s.%s",
							aggrFeatureEvent.getBucketConfName(),
							aggrFeatureEvent.getAggregatedFeatureEventName()),
					aggrFeatureEvent);
			aggrFeatureEvents.add(aggrFeatureEvent.unwrap());
		}

		double entityEventValue = jokerFunction.calculateEntityEventValue(aggrFeatureEventsMap);

		JSONObject entityEvent = new JSONObject();
		entityEvent.put("event_type", "entity_event");
		entityEvent.put(entityEventData.getEntityEventName(), entityEventValue);
		entityEvent.put("date_time_unix", entityEventData.getFiringTimeInSeconds());
		entityEvent.put("start_time_unix", entityEventData.getStartTime());
		entityEvent.put("end_time_unix", entityEventData.getEndTime());
		entityEvent.put("context", entityEventData.getContext());
		entityEvent.put("aggregated_feature_events", aggrFeatureEvents);

		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), entityEvent.toJSONString()));
	}

	// TODO: equals + hash code?
}
