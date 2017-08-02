package presidio.output.domain.records.events;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A basic ADE enriched record. All ADE enriched records (across all data sources) should extend this one.
 * <p>
 * Created by Lior Govrin on 06/06/2017.
 */
@Document
public abstract class EnrichedEvent {

    public static final String EVENT_ID_FIELD = "eventId";
    public static final String SCHEMA_FIELD = "schema";

    @Id
    @Field
    private String id;

    @Field(EVENT_ID_FIELD)
    private String eventId;

    @Field(SCHEMA_FIELD)
    private String schema;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
