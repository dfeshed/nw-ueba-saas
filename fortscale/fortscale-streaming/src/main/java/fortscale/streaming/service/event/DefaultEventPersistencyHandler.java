package fortscale.streaming.service.event;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import net.minidev.json.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by amira on 23/08/2015.
 */
public class DefaultEventPersistencyHandler implements EventPersistencyHandler {

    private static final char COLLECTION_NAME_SEPERATOR = '_';

    private MongoTemplate mongoTemplate;
    private String eventTypeFieldName;

    DefaultEventPersistencyHandler(MongoTemplate mongoTemplate, String eventTypeFieldName) {
        this.mongoTemplate = mongoTemplate;
        this.eventTypeFieldName = eventTypeFieldName;
    }

    @Override
    public void saveEvent(JSONObject event, String collectionPrefix) {
        String eventTypeValue = (String) event.get(eventTypeFieldName);
        String collectionName = new StringBuilder(collectionPrefix).append(COLLECTION_NAME_SEPERATOR).append(eventTypeValue).toString();
        mongoTemplate.getCollection(collectionName).insert((DBObject)JSON.parse(event.toJSONString()));
    }
}
