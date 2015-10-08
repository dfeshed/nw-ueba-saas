package fortscale.streaming.service.event;

import fortscale.domain.core.EntityEvent;
import fortscale.streaming.service.aggregation.entity.event.EntityEventConf;
import fortscale.streaming.service.aggregation.entity.event.EntityEventConfService;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.FIndex;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class EntityEventPersistencyHandler implements EventPersistencyHandler, InitializingBean {
	private static final Logger logger = Logger.getLogger(EntityEventPersistencyHandler.class);
	private static final String COLLECTION_NAME_SEPERATOR = "__";

	@Autowired
	private EventPersistencyHandlerFactory eventPersistencyHandlerFactory;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private EntityEventConfService entityEventConfService;

	@Value("${streaming.event.field.type.entity_event}")
	private String eventTypeFieldValue;

	@Value("${streaming.entity_event.field.entity_event_type}")
	private String entityEventTypeFieldName;

	private Set<String> collectionNames;


	@Override
	public void saveEvent(JSONObject event, String collectionPrefix) throws IOException {
		String entityEventType = (String) event.get(entityEventTypeFieldName);
		String collectionName = new StringBuilder(collectionPrefix).append(COLLECTION_NAME_SEPERATOR)
				.append(eventTypeFieldValue).append(COLLECTION_NAME_SEPERATOR)
				.append(entityEventType).toString();

		if (!isCollectionExist(collectionName)) {
			EntityEventConf entityEventConf = entityEventConfService.getEntityEventConf(entityEventType);
			Integer retentionTimeInDays = entityEventConf.getDaysToRetainDocument();

			mongoTemplate.createCollection(collectionName);
			mongoTemplate.indexOps(collectionName).ensureIndex(new FIndex().expire(retentionTimeInDays, TimeUnit.DAYS).on(EntityEvent.ENTITY_EVENT_END_TIME_UNIX_FILED_NAME, Sort.Direction.DESC));
			collectionNames.add(collectionName);
		}

		EntityEvent entityEvent = null;
		entityEvent = EntityEvent.buildEntityEvent(event);
		mongoTemplate.save(entityEvent, collectionName);
	}

	private boolean isCollectionExist(String collectionName){
		return collectionNames.contains(collectionName);
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		eventPersistencyHandlerFactory.register(eventTypeFieldValue, this);
		collectionNames = new HashSet<>(mongoTemplate.getCollectionNames());
	}
}
