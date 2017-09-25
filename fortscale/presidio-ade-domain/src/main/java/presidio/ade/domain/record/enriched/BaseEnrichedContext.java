package presidio.ade.domain.record.enriched;

import org.springframework.data.mongodb.core.mapping.Field;


/**
 * @author Barak Schuster
 */
public abstract class BaseEnrichedContext {
    public static final String EVENT_ID_FIELD_NAME = "eventId";

    @Field(EVENT_ID_FIELD_NAME)
    protected String eventId;

    public BaseEnrichedContext() {
    }

    public BaseEnrichedContext(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
