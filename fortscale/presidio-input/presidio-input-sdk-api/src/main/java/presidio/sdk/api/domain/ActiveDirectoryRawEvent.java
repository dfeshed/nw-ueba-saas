package presidio.sdk.api.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document
public class ActiveDirectoryRawEvent extends AbstractInputDocument {

    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String IS_SECURITY_SENSITIVE_OPERATION_FIELD_NAME = "isSecuritySensitiveOperation";
    public static final String IS_USER_ADMINISTRATOR_FIELD_NAME = "isUserAdministrator";
    public static final String OBJECT_NAME_FIELD_NAME = "objectName";

    @Field(OPERATION_TYPE_FIELD_NAME)
    private ActiveDirectoryOperationType operationType;
    @Field(IS_SECURITY_SENSITIVE_OPERATION_FIELD_NAME)
    private boolean isSecuritySensitiveOperation;
    @Field(IS_USER_ADMINISTRATOR_FIELD_NAME)
    private boolean isUserAdministrator;
    @NotEmpty
    @Field(OBJECT_NAME_FIELD_NAME)
    private String objectName;

    public ActiveDirectoryRawEvent(String[] event) {
        dateTime = Instant.parse(event[0]);
        this.eventId = event[1];
        this.dataSource = event[2];
        this.operationType = ActiveDirectoryOperationType.valueOf(event[3]);
        this.isSecuritySensitiveOperation = Boolean.valueOf(event[4]);
        this.isUserAdministrator = Boolean.valueOf(event[5]);
        this.objectName = event[6];
        this.result = EventResult.valueOf(event[7]);
        this.normalizedUsername = event[8];
    }

    public ActiveDirectoryRawEvent() {
    }

    public ActiveDirectoryRawEvent(ActiveDirectoryOperationType operationType, boolean isSecuritySensitiveOperation,
                                   boolean isUserAdministrator, String objectName, EventResult result,
                                   String normalizesUsername, String eventId) {
        this.operationType = operationType;
        this.isSecuritySensitiveOperation = isSecuritySensitiveOperation;
        this.isUserAdministrator = isUserAdministrator;
        this.objectName = objectName;
        this.result = result;
        this.normalizedUsername = normalizesUsername;
        this.eventId = eventId;
    }

    public ActiveDirectoryRawEvent(Instant dateTime, String dataSource, String normalizedUsername, String eventId,
                                   EventResult result, ActiveDirectoryOperationType operationType,
                                   boolean isSecuritySensitiveOperation, boolean isUserAdministrator,
                                   String objectName) {
        super(dateTime, dataSource, normalizedUsername, eventId, result);
        this.operationType = operationType;
        this.isSecuritySensitiveOperation = isSecuritySensitiveOperation;
        this.isUserAdministrator = isUserAdministrator;
        this.objectName = objectName;
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

    public void setIsSecuritySensitiveOperation(boolean securitySensitiveOperation) {
        isSecuritySensitiveOperation = securitySensitiveOperation;
    }

    public boolean isUserAdministrator() {
        return isUserAdministrator;
    }

    public void setIsUserAdministrator(boolean userAdministrator) {
        isUserAdministrator = userAdministrator;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public String toString() {
        return "ActiveDirectoryRawEvent{" +
                "dataSource='" + dataSource + '\'' +
                ", normalizedUsername='" + normalizedUsername + '\'' +
                ", operationType=" + operationType +
                ", eventId='" + eventId + '\'' +
                ", isSecuritySensitiveOperation=" + isSecuritySensitiveOperation +
                ", result=" + result +
                ", isUserAdministrator=" + isUserAdministrator +
                ", objectName='" + objectName + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}

