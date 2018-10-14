package presidio.sdk.api.domain.rawevents;

import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.domain.core.EventResult;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
public class ActiveDirectoryRawEvent extends AbstractInputDocument {

    public static final String OBJECT_ID_FIELD_NAME = "objectId";
    public static final String USER_ID_FIELD_NAME = "userId";
    public static final String RESULT_FIELD_NAME = "result";
    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String OPERATION_TYPE_CATEGORIES_FIELD_NAME = "operationTypeCategories";
    public static final String USER_NAME_FIELD_NAME = "userName";
    public static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
    public static final String RESULT_CODE_FIELD_NAME = "resultCode";
    public static final String IS_USER_ADMIN_FIELD_NAME = "isUserAdmin";

    @Field(IS_USER_ADMIN_FIELD_NAME)
    private boolean isUserAdmin;

    @Field(OBJECT_ID_FIELD_NAME)
    private String objectId;

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

    @Field(RESULT_CODE_FIELD_NAME)
    protected String resultCode;

    {
        additionalInfo = new HashMap<>();
        additionalInfo.put(IS_USER_ADMIN_FIELD_NAME, Boolean.toString(false));
    }

    public ActiveDirectoryRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType,
                                   List<String> operationTypeCategories, EventResult result, String userName,
                                   String userDisplayName, Map<String, String> additionalInfo, boolean isUserAdmin,
                                   String objectId, String resultCode) {
        super(dateTime, eventId, dataSource, additionalInfo);
        this.isUserAdmin = isUserAdmin;
        this.objectId = objectId;
        this.userId = userId;
        this.operationType = operationType;
        this.operationTypeCategories = operationTypeCategories;
        this.result = result;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.resultCode = resultCode;
    }

    public ActiveDirectoryRawEvent() {
    }

    public ActiveDirectoryRawEvent(ActiveDirectoryRawEvent other) {
        super(other);
        this.isUserAdmin = other.isUserAdmin;
        this.objectId = other.objectId;
        this.userId = other.userId;
        this.operationType = other.operationType;
        this.operationTypeCategories = other.operationTypeCategories;
        this.result = other.result;
        this.userName = other.userName;
        this.userDisplayName = other.userDisplayName;
        this.resultCode = other.resultCode;
    }

    @JsonProperty(value = IS_USER_ADMIN_FIELD_NAME)
    public boolean getIsUserAdministrator() {
        return isUserAdmin;
    }

    public void setIsUserAdministrator(boolean userAdministrator) {
        isUserAdmin = userAdministrator;
        additionalInfo.put(IS_USER_ADMIN_FIELD_NAME, Boolean.toString(isUserAdmin));
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isUserAdmin() {
        return isUserAdmin;
    }

    public void setUserAdmin(boolean userAdmin) {
        isUserAdmin = userAdmin;
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

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }


    @Override
    public String toString() {
        return "ActiveDirectoryRawEvent{" +
                "isUserAdmin=" + isUserAdmin +
                ", objectId='" + objectId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", userId='" + userId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", operationTypeCategories=" + operationTypeCategories +
                ", result=" + result +
                ", userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", additionalInfo=" + additionalInfo +
                ", dateTime=" + dateTime +
                '}';
    }
}

