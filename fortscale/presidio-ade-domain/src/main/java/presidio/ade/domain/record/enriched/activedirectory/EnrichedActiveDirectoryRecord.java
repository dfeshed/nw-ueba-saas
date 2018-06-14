package presidio.ade.domain.record.enriched.activedirectory;

import fortscale.common.general.Schema;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;

/**
 * The enriched active directory record POJO.
 */
@Document
@AdeRecordMetadata(adeEventType = Schema.ACTIVE_DIRECTORY)
public class EnrichedActiveDirectoryRecord extends EnrichedRecord {
    public static final String USER_ID_FIELD = "userId";
    public static final String SRC_MACHINE_ID_FIELD = "srcMachineId";
    public static final String SRC_MACHINE_NAME_REGEX_CLUSTER_FIELD = "srcMachineNameRegexCluster";
    public static final String IS_USER_ADMIN_FIELD = "isUserAdmin";
    public static final String OBJECT_ID = "objectId";

    @Field(USER_ID_FIELD)
    private String userId;
    @Field(SRC_MACHINE_ID_FIELD)
    private String srcMachineId;
    @Field(SRC_MACHINE_NAME_REGEX_CLUSTER_FIELD)
    private String srcMachineNameRegexCluster;
    @Field(IS_USER_ADMIN_FIELD)
    private Boolean isUserAdmin;
    @Field(OBJECT_ID)
    private String objectId;

    /**
     * C'tor.
     *
     * @param startInstant The record's logical time
     */
    public EnrichedActiveDirectoryRecord(Instant startInstant) {
        super(startInstant);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getSrcMachineNameRegexCluster() {
        return srcMachineNameRegexCluster;
    }

    public void setSrcMachineNameRegexCluster(String srcMachineNameRegexCluster) {
        this.srcMachineNameRegexCluster = srcMachineNameRegexCluster;
    }

    public Boolean getUserAdmin() {
        return isUserAdmin;
    }

    public void setUserAdmin(Boolean isUserAdmin) {
        this.isUserAdmin = isUserAdmin;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    @Transient
    public String getAdeEventType() {
        return Schema.ACTIVE_DIRECTORY.getName();
    }

    @Transient
    public AdeEnrichedActiveDirectoryContext getContext() {
        return new AdeEnrichedActiveDirectoryContext(this);
    }
}
