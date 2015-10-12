package fortscale.streaming.service.aggregation.entity.event;

import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.utils.mongodb.FIndex;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class EntityEventDataMongoStore implements EntityEventDataStore {
	private static final String COLLECTION_NAME_PREFIX = "entity_event_";
	private static final int EXPIRE_AFTER_DAYS_DEFAULT = 90;

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;

	@Override
	public EntityEventData getEntityEventData(String entityEventName, String contextId, long startTime, long endTime) {
		String collectionName = getCollectionName(entityEventName);
		if (mongoDbUtilService.collectionExists(collectionName)) {
			Query query = new Query();
			query.addCriteria(where(EntityEventData.CONTEXT_ID_FIELD).is(contextId));
			query.addCriteria(where(EntityEventData.START_TIME_FIELD).is(startTime));
			query.addCriteria(where(EntityEventData.END_TIME_FIELD).is(endTime));
			return mongoTemplate.findOne(query, EntityEventData.class, collectionName);
		}

		return null;
	}

	private Query getEntityEventDataWithModifiedAtEpochtimeLteQuery(long modifiedAtEpochtime) {
		Query query = new Query();
		Date modifiedAtDate = new Date(TimestampUtils.convertToMilliSeconds(modifiedAtEpochtime));
		query.addCriteria(where(EntityEventData.MODIFIED_AT_DATE_FIELD).lte(modifiedAtDate));
		query.with(new Sort(Direction.ASC, EntityEventData.START_TIME_FIELD));
		return query;
	}

	@Override
	public List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLte(String entityEventName, long modifiedAtEpochtime) {
		String collectionName = getCollectionName(entityEventName);
		if (mongoDbUtilService.collectionExists(collectionName)) {
			Query query = getEntityEventDataWithModifiedAtEpochtimeLteQuery(modifiedAtEpochtime);
			return mongoTemplate.find(query, EntityEventData.class, collectionName);
		}

		return Collections.emptyList();
	}

	@Override
	public List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLteThatWereNotTransmitted(String entityEventName, long modifiedAtEpochtime) {
		String collectionName = getCollectionName(entityEventName);
		if (mongoDbUtilService.collectionExists(collectionName)) {
			Query query = getEntityEventDataWithModifiedAtEpochtimeLteQuery(modifiedAtEpochtime);
			query.addCriteria(where(EntityEventData.TRANSMITTED_FIELD).is(false));
			return mongoTemplate.find(query, EntityEventData.class, collectionName);
		}

		return Collections.emptyList();
	}

	@Override
	public void storeEntityEventData(EntityEventData entityEventData) {
		String entityEventName = entityEventData.getEntityEventName();
		String collectionName = getCollectionName(entityEventName);

		if (!mongoDbUtilService.collectionExists(collectionName)) {
			mongoDbUtilService.createCollection(collectionName);

			// Context ID + start time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(EntityEventData.CONTEXT_ID_FIELD, Direction.ASC)
					.on(EntityEventData.START_TIME_FIELD, Direction.ASC));

			// Transmitted + modified at date
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(EntityEventData.TRANSMITTED_FIELD, Direction.ASC)
					.on(EntityEventData.MODIFIED_AT_DATE_FIELD, Direction.ASC));

			// Start time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(EntityEventData.START_TIME_FIELD, Direction.ASC));

			// Modified at date (TTL)
			int daysToRetainDocument = EXPIRE_AFTER_DAYS_DEFAULT;
			EntityEventConf entityEventConf = entityEventConfService.getEntityEventConf(entityEventName);
			if (entityEventConf != null) {
				Integer daysToRetainDocumentInConf = entityEventConf.getDaysToRetainDocument();
				if (daysToRetainDocumentInConf != null) {
					daysToRetainDocument = daysToRetainDocumentInConf;
				}
			}

			mongoTemplate.indexOps(collectionName).ensureIndex(new FIndex()
					.expire(daysToRetainDocument, TimeUnit.DAYS)
					.named(EntityEventData.MODIFIED_AT_DATE_FIELD)
					.on(EntityEventData.MODIFIED_AT_DATE_FIELD, Direction.ASC));
		}

		mongoTemplate.save(entityEventData, collectionName);
	}


	private static String getCollectionName(String entityEventName) {
		return String.format("%s%s", COLLECTION_NAME_PREFIX, entityEventName);
	}
}
