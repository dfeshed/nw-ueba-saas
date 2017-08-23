package presidio.ade.domain.store.smart;

import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.time.TimeRange;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * A mongo based implementation for the {@link SmartDataStore}.
 *
 * @author Lior Govrin
 */
public class SmartDataStoreMongoImpl implements SmartDataStore {
	private static final String COLLECTION_NAME_PREFIX = "smart_";

	private final MongoDbBulkOpUtil mongoDbBulkOpUtil;
	private final MongoTemplate mongoTemplate;

	public SmartDataStoreMongoImpl(MongoDbBulkOpUtil mongoDbBulkOpUtil, MongoTemplate mongoTemplate) {
		this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void storeSmartRecords(String smartRecordConfName, Collection<SmartRecord> smartRecords) {
		mongoDbBulkOpUtil.insertUnordered(new ArrayList<>(smartRecords), getCollectionName(smartRecordConfName));
	}

	@Override
	public List<EntityEvent> readSmartRecords(TimeRange timeRange, int scoreThreshold) {
		Instant start = timeRange.getStart();
		Instant end = timeRange.getEnd();
		Query query = new Query()
				.addCriteria(where(EntityEvent.ENTITY_EVENT_START_TIME_UNIX_FIELD_NAME).gte(start.getEpochSecond()))
				.addCriteria(where(EntityEvent.ENTITY_EVENT_END_TIME_UNIX_FIELD_NAME).lte(end.getEpochSecond()))
				.addCriteria(where(EntityEvent.ENTITY_EVENT_SCORE_FIELD_NAME).gte(scoreThreshold));
		String collectionName = "ADE_SMARTS";
		return mongoTemplate.find(query, EntityEvent.class, collectionName);
	}

	private static String getCollectionName(String smartRecordConfName) {
		return COLLECTION_NAME_PREFIX + smartRecordConfName;
	}
}
