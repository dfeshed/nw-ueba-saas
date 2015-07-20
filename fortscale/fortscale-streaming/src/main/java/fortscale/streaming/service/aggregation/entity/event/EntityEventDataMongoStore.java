package fortscale.streaming.service.aggregation.entity.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;
import java.util.Collections;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class EntityEventDataMongoStore implements EntityEventDataStore {
	private static final String COLLECTION_NAME = "entity_event_data";

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

	@Override
	public List<EntityEventData> getEntityEventData(String entityEventName, long firingTimeInSeconds) {
		if (mongoTemplate.collectionExists(COLLECTION_NAME)) {
			Query query = new Query();
			query.addCriteria(where(EntityEventData.ENTITY_EVENT_NAME_FIELD).is(entityEventName));
			query.addCriteria(where(EntityEventData.FIRING_TIME_IN_SECONDS_FIELD).lte(firingTimeInSeconds));
			return mongoTemplate.find(query, EntityEventData.class, COLLECTION_NAME);
		}

		return Collections.emptyList();
	}

	@Override
	public void storeEntityEventData(EntityEventData entityEventData) {
		if (!mongoTemplate.collectionExists(COLLECTION_NAME)) {
			mongoTemplate.createCollection(COLLECTION_NAME);
			mongoTemplate.indexOps(COLLECTION_NAME).ensureIndex(new Index().on(EntityEventData.CONTEXT_ID_FIELD, Direction.ASC));
			mongoTemplate.indexOps(COLLECTION_NAME).ensureIndex(new Index().on(EntityEventData.FIRING_TIME_IN_SECONDS_FIELD, Direction.ASC));
		}

		mongoTemplate.save(entityEventData, COLLECTION_NAME);
	}
}
