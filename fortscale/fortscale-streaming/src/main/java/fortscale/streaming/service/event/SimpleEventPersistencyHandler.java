package fortscale.streaming.service.event;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public abstract class SimpleEventPersistencyHandler implements EventPersistencyHandler, InitializingBean {
	private static final String COLLECTION_NAME_SEPARATOR = "_";

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private EventPersistencyHandlerFactory eventPersistencyHandlerFactory;

	@Override
	public void saveEvent(JSONObject event) {
		String collectionName = getCollectionNamePrefix().concat(COLLECTION_NAME_SEPARATOR).concat(getEventType());
		mongoTemplate.getCollection(collectionName).insert((DBObject)JSON.parse(event.toJSONString()));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		eventPersistencyHandlerFactory.register(getEventType(), this);
	}

	public abstract String getCollectionNamePrefix();
	public abstract String getEventType();
}
