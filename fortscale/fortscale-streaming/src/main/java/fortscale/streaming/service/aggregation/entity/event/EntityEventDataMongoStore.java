package fortscale.streaming.service.aggregation.entity.event;

import fortscale.utils.mongodb.FIndex;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class EntityEventDataMongoStore implements EntityEventDataStore {
	private static final String COLLECTION_NAME = "entity_event_data";
	private static final long EXPIRE_AFTER_DAYS_DEFAULT = 365;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public EntityEventData getEntityEventData(String entityEventName, String contextId, long startTime, long endTime) {
		if (mongoTemplate.collectionExists(COLLECTION_NAME)) {
			Query query = new Query();
			query.addCriteria(where(EntityEventData.ENTITY_EVENT_NAME_FIELD).is(entityEventName));
			query.addCriteria(where(EntityEventData.CONTEXT_ID_FIELD).is(contextId));
			query.addCriteria(where(EntityEventData.START_TIME_FIELD).is(startTime));
			query.addCriteria(where(EntityEventData.END_TIME_FIELD).is(endTime));
			return mongoTemplate.findOne(query, EntityEventData.class, COLLECTION_NAME);
		}

		return null;
	}

	private Query getEntityEventDataWithModifiedAtEpochtimeLteQuery(String entityEventName, long modifiedAtEpochtime) {
		Query query = new Query();
		query.addCriteria(where(EntityEventData.ENTITY_EVENT_NAME_FIELD).is(entityEventName));
		Date modifiedAtDate = new Date(TimestampUtils.convertToMilliSeconds(modifiedAtEpochtime));
		query.addCriteria(where(EntityEventData.MODIFIED_AT_DATE_FIELD).lte(modifiedAtDate));
		query.with(new Sort(Direction.ASC, EntityEventData.START_TIME_FIELD));
		return query;
	}

	@Override
	public List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLte(String entityEventName, long modifiedAtEpochtime) {
		if (mongoTemplate.collectionExists(COLLECTION_NAME)) {
			Query query = getEntityEventDataWithModifiedAtEpochtimeLteQuery(entityEventName, modifiedAtEpochtime);
			return mongoTemplate.find(query, EntityEventData.class, COLLECTION_NAME);
		}

		return Collections.emptyList();
	}

	@Override
	public List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLteThatWereNotTransmitted(String entityEventName, long modifiedAtEpochtime) {
		if (mongoTemplate.collectionExists(COLLECTION_NAME)) {
			Query query = getEntityEventDataWithModifiedAtEpochtimeLteQuery(entityEventName, modifiedAtEpochtime);
			query.addCriteria(where(EntityEventData.TRANSMITTED_FIELD).is(false));
			return mongoTemplate.find(query, EntityEventData.class, COLLECTION_NAME);
		}

		return Collections.emptyList();
	}

	@Override
	public void storeEntityEventData(EntityEventData entityEventData) {
		if (!mongoTemplate.collectionExists(COLLECTION_NAME)) {
			mongoTemplate.createCollection(COLLECTION_NAME);

			// Context ID + start time
			mongoTemplate.indexOps(COLLECTION_NAME).ensureIndex(new Index()
					.on(EntityEventData.CONTEXT_ID_FIELD, Direction.ASC)
					.on(EntityEventData.START_TIME_FIELD, Direction.ASC));

			// Transmitted + modified at date
			mongoTemplate.indexOps(COLLECTION_NAME).ensureIndex(new Index()
					.on(EntityEventData.TRANSMITTED_FIELD, Direction.ASC)
					.on(EntityEventData.MODIFIED_AT_DATE_FIELD, Direction.ASC));

			// Start time
			mongoTemplate.indexOps(COLLECTION_NAME).ensureIndex(new Index()
					.on(EntityEventData.START_TIME_FIELD, Direction.ASC));

			// Modified at date (TTL)
			mongoTemplate.indexOps(COLLECTION_NAME).ensureIndex(new FIndex()
					.expire(EXPIRE_AFTER_DAYS_DEFAULT, TimeUnit.DAYS)
					.on(EntityEventData.MODIFIED_AT_DATE_FIELD, Direction.ASC));
		}

		mongoTemplate.save(entityEventData, COLLECTION_NAME);
	}
}
