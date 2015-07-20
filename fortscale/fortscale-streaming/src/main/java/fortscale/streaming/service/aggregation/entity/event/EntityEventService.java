package fortscale.streaming.service.aggregation.entity.event;

import fortscale.utils.ConversionUtils;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.samza.task.MessageCollector;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class EntityEventService {
	private static final Logger logger = Logger.getLogger(EntityEventService.class);
	private static final String SECONDS_TO_WAIT_BEFORE_FIRING_JSON_FIELD = "secondsToWaitBeforeFiring";
	private static final String FIRE_EVENTS_EVERY_SECONDS_JSON_FIELD = "fireEventsEverySeconds";

	private Long secondsToWaitBeforeFiring;
	private Long fireEventsEverySeconds;
	private Long lastTimeEventsWereFired;
	// Mapping from a full aggregated feature event name
	// (bucket conf name -> aggregated feature event name)
	// to its related entity event builders
	private Map<String, Map<String, Set<EntityEventBuilder>>> fullEventNameToBuilders;

	@Autowired
	private EntityEventConfService entityEventConfService;

	public EntityEventService() {
		getGlobalParams();
		lastTimeEventsWereFired = -1L;
		createEntityEventBuilders();
	}

	public void process(JSONObject message) {
		AggrFeatureEventWrapper aggrFeatureEvent = new AggrFeatureEventWrapper(message);

		Map<String, Set<EntityEventBuilder>> eventNameToBuilders = fullEventNameToBuilders.get(aggrFeatureEvent.getBucketConfName());
		if (eventNameToBuilders != null) {
			Set<EntityEventBuilder> builders = eventNameToBuilders.get(aggrFeatureEvent.getAggregatedFeatureEventName());
			if (builders != null) {
				for (EntityEventBuilder builder : builders) {
					builder.updateEntityEventData(aggrFeatureEvent);
				}
			}
		}
	}

	public void window(long currentTimeInMillis, String outputTopic, MessageCollector collector) {
		long currentTimeInSeconds = currentTimeInMillis / 1000;
		if (lastTimeEventsWereFired + fireEventsEverySeconds <= currentTimeInSeconds) {
			for (EntityEventBuilder entityEventBuilder : getAllEntityEventBuilders()) {
				entityEventBuilder.fireEntityEvents(currentTimeInSeconds, outputTopic, collector);
			}

			lastTimeEventsWereFired = currentTimeInSeconds;
		}
	}

	private void getGlobalParams() {
		Map<String, Object> globalParams = entityEventConfService.getGlobalParams();
		String errorMsg;

		secondsToWaitBeforeFiring = ConversionUtils.convertToLong(globalParams.get(SECONDS_TO_WAIT_BEFORE_FIRING_JSON_FIELD));
		if (secondsToWaitBeforeFiring == null) {
			errorMsg = String.format("Missing valid long value for field %s", SECONDS_TO_WAIT_BEFORE_FIRING_JSON_FIELD);
			logger.error(errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}

		fireEventsEverySeconds = ConversionUtils.convertToLong(globalParams.get(FIRE_EVENTS_EVERY_SECONDS_JSON_FIELD));
		if (fireEventsEverySeconds == null) {
			errorMsg = String.format("Missing valid long value for field %s", FIRE_EVENTS_EVERY_SECONDS_JSON_FIELD);
			logger.error(errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}
	}

	private void createEntityEventBuilders() {
		List<EntityEventConf> entityEventDefinitions = entityEventConfService.getEntityEventDefinitions();
		fullEventNameToBuilders = new HashMap<>();

		// Iterate all entity event definitions
		for (EntityEventConf entityEventConf : entityEventDefinitions) {
			// Create new entity event builder
			EntityEventBuilder entityEventBuilder = new EntityEventBuilder(secondsToWaitBeforeFiring, entityEventConf);

			// Add the new builder to the mapping of each aggregated feature event in the conf
			for (String fullEventName : entityEventConf.getAllAggregatedFeatureEventNames()) {
				String[] bucketConfAndEvent = StringUtils.split(fullEventName, '.');

				if (bucketConfAndEvent.length != 2) {
					// Ignore illegal full aggregated feature event name
					continue;
				}

				String bucketConfName = bucketConfAndEvent[0];
				String eventName = bucketConfAndEvent[1];

				Map<String, Set<EntityEventBuilder>> eventNameToBuilders = fullEventNameToBuilders.get(bucketConfName);
				// In case there isn't yet a mapping for this bucket configuration
				if (eventNameToBuilders == null) {
					eventNameToBuilders = new HashMap<>();
					fullEventNameToBuilders.put(bucketConfName, eventNameToBuilders);
				}

				Set<EntityEventBuilder> builders = eventNameToBuilders.get(eventName);
				// In case there isn't yet a mapping for this aggregated feature event
				if (builders == null) {
					builders = new HashSet<>();
					eventNameToBuilders.put(eventName, builders);
				}

				builders.add(entityEventBuilder);
			}
		}
	}

	private Set<EntityEventBuilder> getAllEntityEventBuilders() {
		Set<EntityEventBuilder> allEntityEventBuilders = new HashSet<>();
		for (Map<String, Set<EntityEventBuilder>> eventNameToBuilders : fullEventNameToBuilders.values()) {
			for (Set<EntityEventBuilder> builders : eventNameToBuilders.values()) {
				allEntityEventBuilders.addAll(builders);
			}
		}

		return allEntityEventBuilders;
	}
}
