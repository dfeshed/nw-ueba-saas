package fortscale.streaming.service.task;

import fortscale.persistency.EventPersistencyHandler;
import fortscale.persistency.EventPersistencyHandlerFactory;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;




@Configurable(preConstruction=true)
public class EventScoringPersistencyTaskService {

    @Autowired
    private EventPersistencyHandlerFactory eventPersistencyHandlerFactory;

    @Value("${streaming.event.field.type}")
    private String eventTypeFieldName;

    public void saveEvent(JSONObject event) throws IOException {
        String eventTypeValue = (String) event.get(eventTypeFieldName);
        if(StringUtils.isBlank(eventTypeValue)){
            return; //raw events are saved in hdfs. currently raw events don't have event type value in the message, so isBlank is the condition.
        }

        EventPersistencyHandler eventPersistencyHandler = eventPersistencyHandlerFactory.getEventPersitencyHandler(event);
        if (eventPersistencyHandler != null) {
            eventPersistencyHandler.saveEvent(event);
        }
    }
}
