package fortscale.streaming.service.event;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amira on 23/08/2015.
 */
public class EventPersistencyHandlerFactory implements InitializingBean {

    private static final String AGGR_EVENT_TYPE = "aggr_event";
    private static final String DEFAULT_EVENT_PERCICTENCY_HANDLER = "default_event_persistency_handler";

    private Map<String, EventPersistencyHandler> handlers = new HashMap<>();

    @Value("${streaming.event.field.type}")
    private String eventTypeFieldName;

    @Value("${streaming.event.collection.time.ratio:100}")
    private int eventCollectionTimeRatio;

    @Value("${streaming.event.collection.retention.time:38880000}") // 15 Months = 15*30*24*60*60
    private int eventCollectionRetentionTime;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        handlers.put(DEFAULT_EVENT_PERCICTENCY_HANDLER, new DefaultEventPersistencyHandler(mongoTemplate, eventTypeFieldName));
    }

    public EventPersistencyHandler getEventPersitencyHandler(JSONObject event) {
        String eventTypeValue = (String) event.get(eventTypeFieldName);

        EventPersistencyHandler handler = handlers.get(eventTypeValue);

        if (handler == null) {
            if (eventTypeValue.equals(AGGR_EVENT_TYPE)) {
                handler = new AggregatedEventPersistencyHandler(mongoTemplate, eventTypeFieldName);
                handlers.put(eventTypeValue, handler);
                return handler;
            } else { // return default handler
                handler = handlers.get(DEFAULT_EVENT_PERCICTENCY_HANDLER);
            }
        }

        return handler;
    }


}
