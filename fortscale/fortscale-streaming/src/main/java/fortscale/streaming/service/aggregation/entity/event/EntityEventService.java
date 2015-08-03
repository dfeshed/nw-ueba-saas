package fortscale.streaming.service.aggregation.entity.event;

import fortscale.utils.ConversionUtils;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.samza.task.MessageCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import java.util.*;

@Configurable(preConstruction = true)
public class EntityEventService {
	private static final Logger logger = Logger.getLogger(EntityEventService.class);
	private static final String SECONDS_TO_WAIT_BEFORE_FIRING_JSON_FIELD = "secondsToWaitBeforeFiring";
	private static final String FIRE_EVENTS_EVERY_SECONDS_JSON_FIELD = "fireEventsEverySeconds";

	private Long secondsToWaitBeforeFiring;
	private Long fireEventsEverySeconds;
	private Long lastTimeEventsWereFired;
	// Mapping from a full event name (bucketConfName.aggregatedFeatureEventName) to its related entity event builders
	private Map<String, Set<EntityEventBuilder>> fullEventNameToBuilders;

	@Autowired
	private EntityEventConfService entityEventConfService;

	public EntityEventService() {
		getGlobalParams();
		lastTimeEventsWereFired = -1L;
		createEntityEventBuilders();
	}

	public void process(JSONObject message) {
		AggrFeatureEventWrapper aggrFeatureEvent = new AggrFeatureEventWrapper(message);

		Set<EntityEventBuilder> builders = fullEventNameToBuilders.get(getFullEventName(aggrFeatureEvent));
		if (builders != null) {
			for (EntityEventBuilder builder : builders) {
				builder.updateEntityEventData(aggrFeatureEvent);
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
				Set<EntityEventBuilder> builders = fullEventNameToBuilders.get(fullEventName);
				// In case there isn't yet a mapping for this aggregated feature event
				if (builders == null) {
					builders = new HashSet<>();
					fullEventNameToBuilders.put(fullEventName, builders);
				}

				builders.add(entityEventBuilder);
			}
		}
	}

	private Set<EntityEventBuilder> getAllEntityEventBuilders() {
		Set<EntityEventBuilder> allEntityEventBuilders = new HashSet<>();
		for (Set<EntityEventBuilder> builders : fullEventNameToBuilders.values()) {
			allEntityEventBuilders.addAll(builders);
		}

		return allEntityEventBuilders;
	}

	private String getFullEventName(AggrFeatureEventWrapper aggrFeatureEvent) {
		return String.format("%s.%s", aggrFeatureEvent.getBucketConfName(), aggrFeatureEvent.getAggregatedFeatureName());
	}
}
