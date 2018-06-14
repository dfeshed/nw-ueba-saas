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
    public static final String USER_ID_FIELD_NAME = "userId";
    public static final String EVENT_ID_FIELD_NAME = "eventId";
    public static final String RESULT_FIELD_NAME = "result";
    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String OPERATION_TYPE_CATEGORIES_FIELD_NAME = "operationTypeCategories";
    public static final String USER_NAME_FIELD_NAME = "userName";
    public static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
    public static final String ADDITIONAL_INFO_FIELD_NAME = "additionalInfo";
    public static final String RESULT_CODE_FIELD_NAME = "resultCode";
    public static final String IS_USER_ADMIN_FIELD_NAME = "isUserAdmin";

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

    @Field(OPERATION_TYPE_CATEGORIES_FIELD_NAME)
    protected List<String> operationTypeCategories;

    @Field(RESULT_FIELD_NAME)
    protected EventResult result;

    @Field(USER_NAME_FIELD_NAME)
    protected String userName;

    @Field(USER_DISPLAY_NAME_FIELD_NAME)
    protected String userDisplayName;

    @Field(ADDITIONAL_INFO_FIELD_NAME)
    protected Map<String, String> additionalInfo;

    @Field(RESULT_CODE_FIELD_NAME)
    protected String resultCode;

    {
        additionalInfo = new HashMap<>();
        additionalInfo.put(IS_USER_ADMIN_FIELD_NAME, Boolean.toString(false));
    }


    public AbstractInputDocument(AbstractInputDocument other) {
        super(other);
        this.eventId = other.eventId;
        this.dataSource = other.dataSource;
        this.userId = other.userId;
        this.operationType = other.operationType;
        this.operationTypeCategories = other.operationTypeCategories;
        this.result = other.result;
        this.userName = other.userName;
        this.userDisplayName = other.userDisplayName;
        this.additionalInfo = other.additionalInfo;
        this.resultCode = other.resultCode;
    }

    public AbstractInputDocument() {

    }

    public AbstractInputDocument(Instant dateTime, String eventId, String dataSource, String userId,
                                 String operationType, List<String> operationTypeCategories, EventResult result,
                                 String userName, String userDisplayName, Map<String, String> additionalInfo, String resultCode) {
        super(dateTime);
        this.eventId = eventId;
        this.dataSource = dataSource;
        this.userId = userId;
        this.operationType = operationType;
        this.operationTypeCategories = operationTypeCategories;
        this.result = result;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.additionalInfo = additionalInfo;
        this.resultCode = resultCode;
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

    public List<String> getOperationTypeCategories() {
        return operationTypeCategories;
    }

    public void setOperationTypeCategories(List<String> operationTypeCategories) {
        this.operationTypeCategories = operationTypeCategories;
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

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "AbstractInputDocument{" +
                "eventId='" + eventId + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", userId='" + userId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", operationTypeCategories=" + operationTypeCategories +
                ", result=" + result +
                ", userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", additionalInfo=" + additionalInfo +
                ", dateTime=" + dateTime +
                ", resultCode=" + resultCode +
                '}';
    }
}
