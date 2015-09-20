package fortscale.streaming.service.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import net.minidev.json.JSONObject;

/**
 * Created by amira on 23/08/2015.
 */
public abstract class SimpleEventPersistencyHandler implements EventPersistencyHandler, InitializingBean{

    private static final char COLLECTION_NAME_SEPERATOR = '_';
    
    @Autowired
    private EventPersistencyHandlerFactory eventPersistencyHandlerFactory;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveEvent(JSONObject event, String collectionPrefix) {
        String collectionName = new StringBuilder(collectionPrefix).append(COLLECTION_NAME_SEPERATOR).append(getEventTypeFieldValue()).toString();
        mongoTemplate.getCollection(collectionName).insert((DBObject)JSON.parse(event.toJSONString()));
    }
    
    public abstract String getEventTypeFieldValue();




	@Override
	public void afterPropertiesSet() throws Exception {
		eventPersistencyHandlerFactory.register(getEventTypeFieldValue(), this);
	}
}
