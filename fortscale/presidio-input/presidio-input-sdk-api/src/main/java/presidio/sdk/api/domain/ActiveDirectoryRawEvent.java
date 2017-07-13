package presidio.sdk.api.domain;

import fortscale.domain.core.AbstractAuditableDocument;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document
public class ActiveDirectoryRawEvent extends AbstractAuditableDocument {

    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String IS_SECURITY_SENSITIVE_OPERATION_FIELD_NAME = "isSecuritySensitiveOperation";
    public static final String IS_USER_ADMINISTRATOR_FIELD_NAME = "isUserAdministrator";
    public static final String OBJECT_NAME_FIELD_NAME = "objectName";
    public static final String RESULT_FIELD_NAME = "result";
    public static final String NORMALIZED_USERNAME_FIELD_NAME = "normalizesUsername";
    private static final String EVENT_ID_FIELD_NAME = "eventId";

    @Field(OPERATION_TYPE_FIELD_NAME)
    private ActiveDirectoryOperationType operationType;
    @Field(IS_SECURITY_SENSITIVE_OPERATION_FIELD_NAME)
    private boolean isSecuritySensitiveOperation;
    @Field(IS_USER_ADMINISTRATOR_FIELD_NAME)
    private boolean isUserAdministrator;
    @NotEmpty
    @Field(OBJECT_NAME_FIELD_NAME)
    private String objectName;
    @Field(RESULT_FIELD_NAME)
    private EventResult result;
    @NotEmpty
    @Field(NORMALIZED_USERNAME_FIELD_NAME)
    private String normalizesUsername;
    @NotEmpty
    @Field(EVENT_ID_FIELD_NAME)
    private String eventId;

    public ActiveDirectoryRawEvent(ActiveDirectoryOperationType operationType, boolean isSecuritySensitiveOperation,
                                   boolean isUserAdministrator, String objectName, EventResult result,
                                   String normalizesUsername, String eventId) {
        this.operationType = operationType;
        this.isSecuritySensitiveOperation = isSecuritySensitiveOperation;
        this.isUserAdministrator = isUserAdministrator;
        this.objectName = objectName;
        this.result = result;
        this.normalizesUsername = normalizesUsername;
        this.eventId = eventId;
    }

    public ActiveDirectoryRawEvent(String[] event) {
        dateTime = Instant.parse(event[0]);
        this.eventId = event[1];
        this.operationType = ActiveDirectoryOperationType.valueOf(event[2]);
        this.isSecuritySensitiveOperation = Boolean.valueOf(event[3]);
        this.isUserAdministrator = Boolean.valueOf(event[4]);
        this.objectName = event[5];
        this.result = EventResult.valueOf(event[6]);
        this.normalizesUsername = event[7];
    }

    public ActiveDirectoryRawEvent() {
    }

    public ActiveDirectoryOperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(ActiveDirectoryOperationType operationType) {
        this.operationType = operationType;
    }

    public boolean isSecuritySensitiveOperation() {
        return isSecuritySensitiveOperation;
    }

    public void setSecuritySensitiveOperation(boolean securitySensitiveOperation) {
        isSecuritySensitiveOperation = securitySensitiveOperation;
    }

    public boolean isUserAdministrator() {
        return isUserAdministrator;
    }

    public void setUserAdministrator(boolean userAdministrator) {
        isUserAdministrator = userAdministrator;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }

    public String getNormalizesUsername() {
        return normalizesUsername;
    }

    public void setNormalizesUsername(String normalizesUsername) {
        this.normalizesUsername = normalizesUsername;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}

