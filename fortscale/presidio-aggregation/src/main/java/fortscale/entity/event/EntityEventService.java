package fortscale.entity.event;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;

import java.util.*;
import java.util.concurrent.TimeoutException;

public class EntityEventService {
	private static final Logger logger = Logger.getLogger(EntityEventService.class);
	private static final String SECONDS_TO_WAIT_BEFORE_FIRING_JSON_FIELD = "secondsToWaitBeforeFiring";
	private static final String FIRE_EVENTS_EVERY_SECONDS_JSON_FIELD = "fireEventsEverySeconds";

	private Long secondsToWaitBeforeFiring;
	private Long fireEventsEverySeconds;
	private Long lastTimeEventsWereFired;
	// Mapping from a full event name (bucketConfName.aggregatedFeatureEventName) to its related entity event builders
	private Map<String, Set<EntityEventBuilder>> fullEventNameToBuilders;

	private EntityEventConfService entityEventConfService;
	private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;
	private EntityEventBuilderFactory entityEventBuilderFactory;


	public EntityEventService(EntityEventConfService entityEventConfService,
							  AggrFeatureEventBuilderService aggrFeatureEventBuilderService,
							  EntityEventBuilderFactory entityEventBuilderFactory) {
		this.entityEventConfService = entityEventConfService;
		this.aggrFeatureEventBuilderService = aggrFeatureEventBuilderService;
		this.entityEventBuilderFactory = entityEventBuilderFactory;
//		getGlobalParams();
		lastTimeEventsWereFired = -1L;
		createEntityEventBuilders();
	}

	public void process(JSONObject message) {
		AggrEvent aggrFeatureEvent = aggrFeatureEventBuilderService.buildEvent(message);

		Set<EntityEventBuilder> builders = fullEventNameToBuilders.get(getFullEventName(aggrFeatureEvent));
		if (builders != null) {
			for (EntityEventBuilder builder : builders) {
				builder.updateEntityEventData(aggrFeatureEvent);
			}
		}
	}

	public void sendNewEntityEventsAndUpdateStore(long currentTimeInMillis, IEntityEventSender sender) throws TimeoutException {
		long currentTimeInSeconds = TimestampUtils.convertToSeconds(currentTimeInMillis);
		if (lastTimeEventsWereFired + fireEventsEverySeconds <= currentTimeInSeconds) {
			//todo: getMetrics().sendNewEntityEventAndUpdateStore++;
			//todo: getMetrics().NewEntityEventsSendAndUpdateStoreTime = currentTimeInMillis;
			for (EntityEventBuilder entityEventBuilder : getAllEntityEventBuilders()) {
				entityEventBuilder.sendNewEntityEventsAndUpdateStore(currentTimeInSeconds, sender);
			}

			lastTimeEventsWereFired = currentTimeInSeconds;
		}
	}

	public void sendEntityEventsInTimeRange(Date startTime, Date endTime, long currentTimeInMillis,
											IEntityEventSender sender, boolean updateStore) throws TimeoutException {
		long currentTimeInSeconds = TimestampUtils.convertToSeconds(currentTimeInMillis);
		//todo: getMetrics().sendEntityEventsInTimeRange++;
		for (EntityEventBuilder entityEventBuilder : getAllEntityEventBuilders()) {
			entityEventBuilder.sendEntityEventsInTimeRange(startTime, endTime, currentTimeInSeconds, sender, updateStore);
		}
	}

//	private void getGlobalParams() {
//		Map<String, Object> globalParams = entityEventConfService.getGlobalParams();
//		String errorMsg;
//
//		secondsToWaitBeforeFiring = ConversionUtils.convertToLong(globalParams.get(SECONDS_TO_WAIT_BEFORE_FIRING_JSON_FIELD));
//		if (secondsToWaitBeforeFiring == null) {
//			errorMsg = String.format("Missing valid long value for field %s", SECONDS_TO_WAIT_BEFORE_FIRING_JSON_FIELD);
//			logger.error(errorMsg);
//			throw new IllegalArgumentException(errorMsg);
//		}
//
//		fireEventsEverySeconds = ConversionUtils.convertToLong(globalParams.get(FIRE_EVENTS_EVERY_SECONDS_JSON_FIELD));
//		if (fireEventsEverySeconds == null) {
//			errorMsg = String.format("Missing valid long value for field %s", FIRE_EVENTS_EVERY_SECONDS_JSON_FIELD);
//			logger.error(errorMsg);
//			throw new IllegalArgumentException(errorMsg);
//		}
//	}

	private void createEntityEventBuilders() {
		List<EntityEventConf> entityEventDefinitions = entityEventConfService.getEntityEventDefinitions();
		fullEventNameToBuilders = new HashMap<>();

		// Iterate all entity event definitions
		for (EntityEventConf entityEventConf : entityEventDefinitions) {
			// Create new entity event builder
			EntityEventBuilder entityEventBuilder = entityEventBuilderFactory.createEntityEventBuilder(secondsToWaitBeforeFiring, entityEventConf);

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
		fullEventNameToBuilders.values().forEach(allEntityEventBuilders::addAll);
		return allEntityEventBuilders;
	}

	private String getFullEventName(AggrEvent aggrFeatureEvent) {
		return String.format("%s.%s", aggrFeatureEvent.getBucketConfName(), aggrFeatureEvent.getAggregatedFeatureName());
	}
}
