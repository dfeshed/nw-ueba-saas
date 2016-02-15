package fortscale.entity.event;

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

public class EntityEventMongoStore {
	private static final String COLLECTION_NAME_PREFIX = "scored_";
	private static final String COLLECTION_NAME_SEPARATOR = "__";
	private static final int SECONDS_IN_DAY = 24 * 60 * 60;

	@Value("${streaming.event.field.type.entity_event}")
	private String eventTypeFieldValue;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;
	@Autowired
	private EntityEventConfService entityEventConfService;

	public void save(EntityEvent entityEvent) {
		ensureCollectionExists(entityEvent);
		mongoTemplate.save(entityEvent, getCollectionName(entityEvent));
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
			long endTimeSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
			Map<Long, List<EntityEvent>> dateToHighestEntityEvents = new HashMap<>(numOfDays);
			while (numOfDays-- > 0) {
				endTimeSeconds -= SECONDS_IN_DAY;
				long startTimeSeconds = endTimeSeconds - SECONDS_IN_DAY;

				Query query = new Query()
						.addCriteria(Criteria.where(EntityEvent.ENTITY_EVENT_START_TIME_UNIX_FIELD_NAME).gte(startTimeSeconds))
						.addCriteria(Criteria.where(EntityEvent.ENTITY_EVENT_START_TIME_UNIX_FIELD_NAME).lt(endTimeSeconds))
						.with(new Sort(Sort.Direction.DESC, EntityEvent.ENTITY_EVENT_UNREDUCED_SCORE_FIELD_NAME))
						.limit(topK);
				dateToHighestEntityEvents.put(startTimeSeconds,
						mongoTemplate.find(query, EntityEvent.class, collectionName));
			}
			return dateToHighestEntityEvents;
		} else {
			return Collections.emptyMap();
		}
	}

	private void ensureCollectionExists(EntityEvent entityEvent) {
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
					.ensureIndex(new Index().named(EntityEvent.ENTITY_EVENT_UNREDUCED_SCORE_FIELD_NAME)
							.on(EntityEvent.ENTITY_EVENT_UNREDUCED_SCORE_FIELD_NAME, Sort.Direction.DESC));
		}
	}
}
