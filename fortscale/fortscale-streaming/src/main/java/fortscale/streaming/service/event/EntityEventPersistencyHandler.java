package fortscale.streaming.service.event;

import org.springframework.beans.factory.annotation.Value;

public class EntityEventPersistencyHandler extends SimpleEventPersistencyHandler{

	
	@Value("${streaming.event.field.type.entity_event}")
    private String eventTypeFieldValue;
	
	
	public String getEventTypeFieldValue() {
		return eventTypeFieldValue;
	}
	
	
}
