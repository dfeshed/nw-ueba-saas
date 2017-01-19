package fortscale.aggregation.feature.bucket.state;

import fortscale.domain.core.AbstractDocument;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = FeatureBucketState.COLLECTION_NAME)
public class FeatureBucketState extends AbstractDocument {
    public static final String COLLECTION_NAME = "FeatureBucketState";

    private static final String LAST_SYNCED_EVENT_DATE_FIELD_NAME = "lastSyncedEventDate";
    private static final String MODIFIED_AT_FIELD = "modifiedAt";

    @Field(LAST_SYNCED_EVENT_DATE_FIELD_NAME)
    private Instant lastSyncedEventDate;

    @LastModifiedDate
    @Field(MODIFIED_AT_FIELD)
    private Instant modifiedAt;

    public FeatureBucketState() {
    }

    public FeatureBucketState(Instant lastSyncedEventDate) {
        this.lastSyncedEventDate = lastSyncedEventDate;
    }

    public Instant getLastSyncedEventDate() {
        return lastSyncedEventDate;
    }

    public void setLastSyncedEventDate(Instant lastSyncedEventDate) {
        this.lastSyncedEventDate = lastSyncedEventDate;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
