package fortscale.entity.event;

import fortscale.aggregation.feature.event.ScoredEventsCounterReader;
import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.domain.core.EntityEvent;
import fortscale.utils.mongodb.FIndex;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class EntityEventMongoStore  implements ScoredEventsCounterReader {
	private static final String COLLECTION_NAME_PREFIX = "scored_";
	private static final String COLLECTION_NAME_SEPARATOR = "__";
	private static final int SECONDS_IN_DAY = 24 * 60 * 60;

	@Value("${streaming.event.field.type.entity_event}")
	private String eventTypeFieldValue;
	@Value("${fortscale.scored.entity.event.store.page.size}")
	private int storePageSize;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;
	@Autowired
	private EntityEventConfService entityEventConfService;

	private Map<String, List<EntityEvent>> collectionToEntityEventListMap = new HashMap<>();
	private List<String> allScoredEntityEventCollectionNames;

	public void save(EntityEvent entityEvent) {
		String collectionName = ensureCollectionExists(entityEvent);
		if (storePageSize > 1) {
			List<EntityEvent> entityEventList = collectionToEntityEventListMap.get(collectionName);
			if (entityEventList == null) {
				entityEventList = new ArrayList<>();
				collectionToEntityEventListMap.put(collectionName, entityEventList);
			}
			entityEventList.add(entityEvent);
			if (entityEventList.size() >= storePageSize) {
				mongoTemplate.insert(entityEventList, collectionName);
				collectionToEntityEventListMap.remove(collectionName);
			}
		} else {
			mongoTemplate.save(entityEvent, collectionName);
		}
	}

	public void flush() {
		if (collectionToEntityEventListMap.isEmpty()) {
			return;
		}
		for(String collectionName: collectionToEntityEventListMap.keySet()) {
			mongoTemplate.insert(collectionToEntityEventListMap.get(collectionName), collectionName);
		}
		collectionToEntityEventListMap = new HashMap<>();
	}

	private String getCollectionName(String entityEventType) {
		return StringUtils.join(
				COLLECTION_NAME_PREFIX, COLLECTION_NAME_SEPARATOR,
				eventTypeFieldValue, COLLECTION_NAME_SEPARATOR,
				entityEventType);
	}

	private String getCollectionName(EntityEvent entityEvent) {
		return getCollectionName(entityEvent.getEntity_event_type());
	}

	public Map<Long, List<EntityEvent>> getDateToTopEntityEvents(String entityEventType, Date endTime, int numOfDays, int topK) {
		String collectionName = getCollectionName(entityEventType);
		if (mongoTemplate.collectionExists(collectionName)) {
			long endTimeSeconds = TimestampUtils.convertToSeconds(endTime);
			Map<Long, List<EntityEvent>> dateToHighestEntityEvents = new HashMap<>(numOfDays);
			while (numOfDays-- > 0) {
				long startTime = endTimeSeconds - SECONDS_IN_DAY;
				Query query = new Query()
						.addCriteria(Criteria.where(EntityEvent.ENTITY_EVENT_END_TIME_UNIX_FIELD_NAME)
								.gt(startTime)
								.lte(endTimeSeconds))
						.with(new Sort(Sort.Direction.DESC, EntityEvent.ENTITY_EVENT_UNREDUCED_SCORE_FIELD_NAME))
						.limit(topK);
				dateToHighestEntityEvents.put(startTime,
						mongoTemplate.find(query, EntityEvent.class, collectionName));
				endTimeSeconds -= SECONDS_IN_DAY;
			}
			return dateToHighestEntityEvents;
		} else {
			return Collections.emptyMap();
		}
	}

	private String ensureCollectionExists(EntityEvent entityEvent) {
		String collectionName = getCollectionName(entityEvent);
		if (!mongoDbUtilService.collectionExists(collectionName)) {
			EntityEventConf entityEventConf = entityEventConfService.getEntityEventConf(entityEvent.getEntity_event_type());
			Integer retentionTimeInDays = entityEventConf.getDaysToRetainDocument();

			mongoDbUtilService.createCollection(collectionName);
			mongoTemplate.indexOps(collectionName)
					.ensureIndex(new FIndex().expire(retentionTimeInDays, TimeUnit.DAYS)
							.named(EntityEvent.ENTITY_EVENT_CREATION_TIME_FIELD_NAME)
							.on(EntityEvent.ENTITY_EVENT_CREATION_TIME_FIELD_NAME, Sort.Direction.DESC));
			mongoTemplate.indexOps(collectionName)
					.ensureIndex(new Index().named(EntityEvent.ENTITY_EVENT_END_TIME_UNIX_FIELD_NAME)
							.on(EntityEvent.ENTITY_EVENT_END_TIME_UNIX_FIELD_NAME, Sort.Direction.DESC));
			mongoTemplate.indexOps(collectionName)
					.ensureIndex(new Index().named(EntityEvent.ENTITY_EVENT_SCORE_FIELD_NAME)
							.on(EntityEvent.ENTITY_EVENT_SCORE_FIELD_NAME, Sort.Direction.DESC));
			mongoTemplate.indexOps(collectionName)
					.ensureIndex(new Index().named(EntityEvent.ENTITY_EVENT_UNREDUCED_SCORE_FIELD_NAME)
							.on(EntityEvent.ENTITY_EVENT_UNREDUCED_SCORE_FIELD_NAME, Sort.Direction.DESC));
		}
		return collectionName;
	}


	@Override
	public long getTotalNumberOfScoredEvents() {
		if(allScoredEntityEventCollectionNames ==null) {
			allScoredEntityEventCollectionNames = new ArrayList<>();
			entityEventConfService.getEntityEventNames().forEach(entityEventName ->
					allScoredEntityEventCollectionNames.add(getCollectionName(entityEventName)));
		}

		long totalNumberOfEvents = 0;

		for(String collectionName: allScoredEntityEventCollectionNames) {
			totalNumberOfEvents += mongoTemplate.count(new Query(), collectionName);
		}

		return totalNumberOfEvents;
	}
}
