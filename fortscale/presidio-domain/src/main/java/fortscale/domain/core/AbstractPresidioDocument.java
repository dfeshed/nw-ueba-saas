package fortscale.domain.core;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class AbstractPresidioDocument extends AbstractAuditableDocument {

    public static final String DATA_SOURCE_FIELD_NAME = "dataSource";
    public static final String USER_ID_FIELD_NAME = "normalizedUsername";
    public static final String EVENT_ID_FIELD_NAME = "eventId";
    public static final String RESULT_FIELD_NAME = "result";
    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String OPERATION_TYPE_CATEGORY_FIELD_NAME = "operationTypeCategory";

    @NotEmpty
    @Field(EVENT_ID_FIELD_NAME)
    protected String eventId;

    @Field(DATA_SOURCE_FIELD_NAME)
    @NotEmpty
    protected String dataSource;

    @Field(USER_ID_FIELD_NAME)
    @NotEmpty
    protected String userId;

    @Field(OPERATION_TYPE_FIELD_NAME)
    @NotEmpty
    protected String operationType;

    @Field(OPERATION_TYPE_CATEGORY_FIELD_NAME)
    protected List<String> operationTypeCategory;

    @Field(RESULT_FIELD_NAME)
    protected EventResult result;

    protected String userName;
    protected String userDisplayName;
    protected Map<String, String> additionalInfo;

    public AbstractPresidioDocument() {

    }

    public AbstractPresidioDocument(Instant dateTime, String eventId, String dataSource, String userId, String operationType, List<String> operationTypeCategory, EventResult result) {
        super(dateTime);
        this.eventId = eventId;
        this.dataSource = dataSource;
        this.userId = userId;
        this.operationType = operationType;
        this.operationTypeCategory = operationTypeCategory;
        this.result = result;
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

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public List<String> getOperationTypeCategory() {
        return operationTypeCategory;
    }

    public void setOperationTypeCategory(List<String> operationTypeCategory) {
        this.operationTypeCategory = operationTypeCategory;
    }


    @Override
    public String toString() {
        return "AbstractPresidioDocument{" +
                "eventId='" + eventId + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", userId='" + userId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", operationTypeCategory=" + operationTypeCategory +
                ", result=" + result +
                "} " + super.toString();
    }
}
