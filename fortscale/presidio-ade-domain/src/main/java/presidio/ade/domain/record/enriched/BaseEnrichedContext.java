package presidio.ade.domain.record.enriched;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import static presidio.ade.domain.record.enriched.file.AdeEnrichedFileContext.EVENT_ID_FIELD_NAME;

/**
 * Created by barak_schuster on 14/08/2017.
 */
public abstract class BaseEnrichedContext {
    @Indexed
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
