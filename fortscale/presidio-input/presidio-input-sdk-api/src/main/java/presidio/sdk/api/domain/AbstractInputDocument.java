package presidio.sdk.api.domain;

import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.domain.core.EventResult;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractInputDocument extends AbstractAuditableDocument {

    public static final String DATA_SOURCE_FIELD_NAME = "dataSource";
    public static final String EVENT_ID_FIELD_NAME = "eventId";
    public static final String ADDITIONAL_INFO_FIELD_NAME = "additionalInfo";

    @NotEmpty
    @Field(EVENT_ID_FIELD_NAME)
    private String eventId;

    @Field(DATA_SOURCE_FIELD_NAME)
    @NotEmpty
    private String dataSource;

    @Field(ADDITIONAL_INFO_FIELD_NAME)
    private Map<String, String> additionalInfo;

    public AbstractInputDocument(AbstractInputDocument other) {
        super(other);
        this.eventId = other.eventId;
        this.dataSource = other.dataSource;
        this.additionalInfo = other.additionalInfo;
    }

    public AbstractInputDocument() {
        additionalInfo = new HashMap<>();
    }

    public AbstractInputDocument(Instant dateTime, String eventId, String dataSource, Map<String, String> additionalInfo) {
        super(dateTime);
        this.eventId = eventId;
        this.dataSource = dataSource;
        this.additionalInfo = additionalInfo;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }


    @Override
    public String toString() {
        return "AbstractInputDocument{" +
                "eventId='" + eventId + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", additionalInfo=" + additionalInfo +
                ", dateTime=" + dateTime +
                '}';
    }
}
