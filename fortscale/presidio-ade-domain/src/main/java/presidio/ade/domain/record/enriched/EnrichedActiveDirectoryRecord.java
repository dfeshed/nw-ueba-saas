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


    public static final String NORMALIZED_USERNAME_FIELD = "normalizedUsername";
    public static final String IS_SECURITY_SENSITIVE_OPERATION_FIELD = "isSecuritySensitiveOperation";
    public static final String IS_USER_ADMINISTRATOR_FIELD = "isUserAdministrator";
    public static final String OBJECT_NAME = "objectName";

    @Indexed
    @Field(NORMALIZED_USERNAME_FIELD)
    private String normalizedUsername;
    @Field(IS_SECURITY_SENSITIVE_OPERATION_FIELD)
    private Boolean isSecuritySensitiveOperation;
    @Field(IS_USER_ADMINISTRATOR_FIELD)
    private Boolean isUserAdministrator;
    @Field(OBJECT_NAME)
    private String objectName;

    /**
     * C'tor.
     *
     * @param startInstant The record's logical time
     */
    public EnrichedActiveDirectoryRecord(Instant startInstant) {
        super(startInstant);
    }

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
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
