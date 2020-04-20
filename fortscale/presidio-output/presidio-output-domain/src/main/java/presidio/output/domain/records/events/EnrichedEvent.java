package presidio.output.domain.records.events;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;


public class EnrichedEvent {

    public static final String EVENT_ID_FIELD_NAME= "eventId";
    public static final String SCHEMA_FIELD_NAME = "schema";
    public static final String DATA_SOURCE_FIELD_NAME = "dataSource";
    public static final String ADDITIONAL_INFO_FIELD_NAME = "additionalInfo";
    public static final String EVENT_DATE_FIELD_NAME = "eventDate";

    @Id
    @Field
    private String id;

    @CreatedDate
    private Instant createdDate;

    @Field(EVENT_DATE_FIELD_NAME)
    @Indexed
    private Instant eventDate;

    @Field(EVENT_ID_FIELD_NAME)
    private String eventId;

    @Field(SCHEMA_FIELD_NAME)
    private String schema;

    @Field(DATA_SOURCE_FIELD_NAME)
    private String dataSource;

    @Field(ADDITIONAL_INFO_FIELD_NAME)
    private Map<String, String> additionalInfo;

    public EnrichedEvent() {
    }

    public EnrichedEvent(Instant createdDate,
                             Instant eventDate,
                             String eventId,
                             String schema,
                             String dataSource,
                             Map<String, String> additionalInfo) {
        this.createdDate = createdDate;
        this.eventDate = eventDate;
        this.eventId = eventId;
        this.schema = schema;
        this.dataSource = dataSource;
        this.additionalInfo = additionalInfo;
    }

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

    public String getId() {
        return id;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }
}
