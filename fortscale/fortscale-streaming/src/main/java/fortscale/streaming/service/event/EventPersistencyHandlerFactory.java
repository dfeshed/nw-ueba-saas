package fortscale.streaming.service.event;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.minidev.json.JSONObject;

/**
 * Created by amira on 23/08/2015.
 */
@Service
public class EventPersistencyHandlerFactory {
    private Map<String, EventPersistencyHandler> handlers = new HashMap<>();
    
    

    @Value("${streaming.event.field.type}")
    private String eventTypeFieldName;

    @Value("${streaming.event.collection.time.ratio:100}")
    private int eventCollectionTimeRatio;

    @Value("${streaming.event.collection.retention.time:38880000}") // 15 Months = 15*30*24*60*60
    private int eventCollectionRetentionTime;

    private EventPersistencyHandler defaultEventPersistencyHandler;

    
    public void register(String eventType, EventPersistencyHandler handler){
    	handlers.put(eventType, handler);
    }
    
    public void registerdefaultEventPersistencyHandler(EventPersistencyHandler handler){
    	defaultEventPersistencyHandler = handler;
    }

    public EventPersistencyHandler getEventPersitencyHandler(JSONObject event) {
        String eventTypeValue = (String) event.get(eventTypeFieldName);

        EventPersistencyHandler handler = handlers.get(eventTypeValue);

        if (handler == null) {
        	handler = defaultEventPersistencyHandler;
        }

        return handler;
    }
}
