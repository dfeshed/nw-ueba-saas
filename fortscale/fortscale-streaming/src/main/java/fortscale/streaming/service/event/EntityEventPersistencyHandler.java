package fortscale.streaming.service.event;

import fortscale.domain.core.EntityEvent;
import fortscale.entity.event.EntityEventMongoStore;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class EntityEventPersistencyHandler implements EventPersistencyHandler, InitializingBean {

	@Value("${streaming.event.field.type.entity_event}")
	private String eventTypeFieldValue;

	@Autowired
	private EventPersistencyHandlerFactory eventPersistencyHandlerFactory;

	@Autowired
	private EntityEventMongoStore entityEventMongoStore;

	@Override
	public void saveEvent(JSONObject event) throws IOException {
		entityEventMongoStore.save(EntityEvent.buildEntityEvent(event));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		eventPersistencyHandlerFactory.register(eventTypeFieldValue, this);
	}
}
