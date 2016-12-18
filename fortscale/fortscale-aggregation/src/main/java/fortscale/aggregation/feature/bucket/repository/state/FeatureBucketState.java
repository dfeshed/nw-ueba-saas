package fortscale.aggregation.feature.bucket.repository.state;

import fortscale.aggregation.feature.bucket.repository.FeatureBucketMetadata;
import fortscale.domain.core.AbstractDocument;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = FeatureBucketMetadata.COLLECTION_NAME)
public class FeatureBucketState extends AbstractDocument {
    public static final String COLLECTION_NAME = "FeatureBucketState";

    private static final String DATE_FIELD = "date";
    private static final String MODIFIED_AT_FIELD = "modifiedAt";

    @Field(DATE_FIELD)
    private Instant lastSyncedDate;

    @LastModifiedDate
    @Field(MODIFIED_AT_FIELD)
    private Instant modifiedAt;

    public FeatureBucketState() {
    }

    public FeatureBucketState(Instant lastSyncedDate) {
        this.lastSyncedDate = lastSyncedDate;
    }

    public Instant getLastSyncedDate() {
        return lastSyncedDate;
    }

    public void setLastSyncedDate(Instant lastSyncedDate) {
        this.lastSyncedDate = lastSyncedDate;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
