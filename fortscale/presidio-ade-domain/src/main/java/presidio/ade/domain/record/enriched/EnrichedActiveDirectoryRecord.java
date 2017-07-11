package presidio.ade.domain.record.enriched;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;

/**
 * The enriched active directory record POJO.
 */
@Document
//todo: add @AdeRecordMetadata annotation
public class EnrichedActiveDirectoryRecord extends EnrichedRecord {

    public static final String OPERATION_TYPE_FIELD = "operationType";
    public static final String NORMALIZED_USERNAME_FIELD = "normalizedUsername";
    public static final String IS_SECURITY_SENSITIVE_OPERATION_FIELD = "isSecuritySensitiveOperation";
    public static final String IS_USER_ADMINISTRATOR_FIELD = "isUserAdministrator";
    public static final String OBJECT_NAME = "objectName";
    public static final String RESULT_FIELD = "result";

    @Indexed
    @Field(NORMALIZED_USERNAME_FIELD)
    private String normalizedUsername;
    @Field(OPERATION_TYPE_FIELD)
    private String operationType;
    @Field(IS_SECURITY_SENSITIVE_OPERATION_FIELD)
    private Boolean isSecuritySensitiveOperation;
    @Field(IS_USER_ADMINISTRATOR_FIELD)
    private Boolean isUserAdministrator;
    @Field(OBJECT_NAME)
    private String objectName;
    @Field(RESULT_FIELD)
    private String result;

    /**
     * C'tor.
     *
     * @param dateTime The record's logical time
     */
    public EnrichedActiveDirectoryRecord(Instant dateTime) {
        super(dateTime);
    }

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Boolean getSecuritySensitiveOperation() {
        return isSecuritySensitiveOperation;
    }

    public void setSecuritySensitiveOperation(Boolean securitySensitiveOperation) {
        isSecuritySensitiveOperation = securitySensitiveOperation;
    }

    public Boolean getUserAdministrator() {
        return isUserAdministrator;
    }

    public void setUserAdministrator(Boolean userAdministrator) {
        isUserAdministrator = userAdministrator;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    @Transient
    public String getAdeEventType() {
        return ActiveDirectoryRecord.ACTIVE_DIRECTORY_STR;
    }

    @Transient
    public AdeEnrichedActiveDirectoryContext getContext() {
        return new AdeEnrichedActiveDirectoryContext(this);
    }
}
