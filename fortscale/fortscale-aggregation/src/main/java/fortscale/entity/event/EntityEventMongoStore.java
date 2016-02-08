package fortscale.entity.event;

import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.domain.core.EntityEvent;
import fortscale.utils.mongodb.FIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import java.util.concurrent.TimeUnit;

public class EntityEventMongoStore {
	private static final String COLLECTION_NAME_PREFIX = "scored___entity_event__";

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

	private String getCollectionName(EntityEvent entityEvent) {
		return String.format("%s%s", COLLECTION_NAME_PREFIX, entityEvent.getEntity_event_type());
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
