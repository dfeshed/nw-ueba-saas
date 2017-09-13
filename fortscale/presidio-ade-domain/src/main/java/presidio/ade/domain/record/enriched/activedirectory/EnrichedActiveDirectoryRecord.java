package presidio.ade.domain.record.enriched.activedirectory;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;

/**
 * The enriched active directory record POJO.
 */
@Document
@AdeRecordMetadata(adeEventType = AdeActiveDirectoryRecord.ACTIVE_DIRECTORY_STR)
@CompoundIndexes({
        @CompoundIndex(def = "{'startInstant': 1}")
        // A compound index is created dynamically for every <'startInstant', 'contextType'> pair in use
})
public class EnrichedActiveDirectoryRecord extends EnrichedRecord {
    public static final String USER_ID_FIELD = "userId";
    public static final String IS_USER_ADMIN_FIELD = "isUserAdmin";
    public static final String OBJECT_ID = "objectId";

    @Indexed
    @Field(USER_ID_FIELD)
    private String userId;
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
        return AdeActiveDirectoryRecord.ACTIVE_DIRECTORY_STR;
    }

    @Transient
    public AdeEnrichedActiveDirectoryContext getContext() {
        return new AdeEnrichedActiveDirectoryContext(this);
    }
}
