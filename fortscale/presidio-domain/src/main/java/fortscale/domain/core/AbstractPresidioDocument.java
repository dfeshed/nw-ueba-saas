package fortscale.domain.core;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class AbstractPresidioDocument extends AbstractAuditableDocument {

    public static final String DATA_SOURCE_FIELD_NAME = "dataSource";
    public static final String NORMALIZED_USERNAME_FIELD_NAME = "normalizedUsername";
    public static final String EVENT_ID_FIELD_NAME = "eventId";
    public static final String RESULT_FIELD_NAME = "result";

    @Field(DATA_SOURCE_FIELD_NAME)
    @NotEmpty
    protected String dataSource;
    @Field(NORMALIZED_USERNAME_FIELD_NAME)

    @NotEmpty
    protected String normalizedUsername;
    @NotEmpty
    @Field(EVENT_ID_FIELD_NAME)
    protected String eventId;
    @Field(RESULT_FIELD_NAME)
    protected EventResult result;

    public AbstractInputDocument() {

    }

    public AbstractInputDocument(Instant dateTime, String dataSource, String normalizedUsername,
                                 String eventId, EventResult result) {
        super(dateTime);
        this.dataSource = dataSource;
        this.normalizedUsername = normalizedUsername;
        this.eventId = eventId;
        this.result = result;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }
}
