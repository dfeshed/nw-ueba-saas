package fortscale.common.event.service;

import fortscale.common.event.EntityEvent;
import fortscale.common.event.Event;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

public class EntityEventService implements EventService {
	private static final String DATA_SOURCE_SEPARATOR = ".";

	@Value("${streaming.event.field.type.entity_event}")
	private String eventTypeEntityEvent;
	@Value("${streaming.entity_event.field.entity_event_type}")
	private String entityEventTypeFieldName;

	@Override
	public Event createEvent(JSONObject message) {
		Assert.notNull(message, "Input message cannot be null.");
		String entityEventType = message.getAsString(entityEventTypeFieldName);

		if (entityEventType == null) {
			throw new IllegalStateException(String.format(
					"Message must contain an %s field: %s.",
					entityEventTypeFieldName, message.toJSONString()));
		}

		return new EntityEvent(message, getDataSource(entityEventType));
	}

	private String getDataSource(String entityEventType) {
		return String.format("%s%s%s", eventTypeEntityEvent, DATA_SOURCE_SEPARATOR, entityEventType);
	}
}
