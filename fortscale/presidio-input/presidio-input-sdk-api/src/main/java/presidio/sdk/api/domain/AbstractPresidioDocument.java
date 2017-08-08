package presidio.sdk.api.domain;

import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.domain.core.EventResult;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class AbstractPresidioDocument extends AbstractAuditableDocument {

    public static final String DATA_SOURCE_FIELD_NAME = "dataSource";
    public static final String USER_ID_FIELD_NAME = "userId";
    public static final String EVENT_ID_FIELD_NAME = "eventId";
    public static final String RESULT_FIELD_NAME = "result";
    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String OPERATION_TYPE_CATEGORY_FIELD_NAME = "operationTypeCategory";
    public static final String USER_NAME_FIELD_NAME = "userName";
    public static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
    public static final String ADDITIONAL_INFO_FIELD_NAME = "additionalInfo";

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

    @Field(USER_NAME_FIELD_NAME)
    protected String userName;

    @Field(USER_DISPLAY_NAME_FIELD_NAME)
    protected String userDisplayName;

    @Field(ADDITIONAL_INFO_FIELD_NAME)
    protected Map<String, String> additionalInfo;

    public AbstractPresidioDocument() {

    }

    public AbstractPresidioDocument(Instant dateTime, String eventId, String dataSource, String userId,
                                    String operationType, List<String> operationTypeCategory, EventResult result,
                                    String userName, String userDisplayName, Map<String, String> additionalInfo) {
        super(dateTime);
        this.eventId = eventId;
        this.dataSource = dataSource;
        this.userId = userId;
        this.operationType = operationType;
        this.operationTypeCategory = operationTypeCategory;
        this.result = result;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
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
                ", userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", additionalInfo=" + additionalInfo +
                ", dateTime=" + dateTime +
                '}';
    }
}
