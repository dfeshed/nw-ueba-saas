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

    private static final String LAST_CLOSED_DAILY_BUCKET_DATE_FIELD_NAME = "lastClosedDailyBucketDate";
    private static final String LAST_SYNCED_EVENT_DATE_FIELD_NAME = "lastSyncedEventDate";
    private static final String MODIFIED_AT_FIELD = "modifiedAt";

    @Field(LAST_CLOSED_DAILY_BUCKET_DATE_FIELD_NAME)
    private Instant lastClosedDailyBucketDate;

    @Field(LAST_SYNCED_EVENT_DATE_FIELD_NAME)
    private Instant lastSyncedEventDate;

    @LastModifiedDate
    @Field(MODIFIED_AT_FIELD)
    private Instant modifiedAt;

    public FeatureBucketState() {
    }

    public FeatureBucketState(Instant lastClosedDailyBucketDate, Instant lastSyncedEventDate) {
        this.lastClosedDailyBucketDate = lastClosedDailyBucketDate;
        this.lastSyncedEventDate = lastSyncedEventDate;
    }

    public Instant getLastSyncedEventDate() {
        return lastSyncedEventDate;
    }

    public void setLastSyncedEventDate(Instant lastSyncedEventDate) {
        this.lastSyncedEventDate = lastSyncedEventDate;
    }

    public Instant getLastClosedDailyBucketDate() {
        return lastClosedDailyBucketDate;
    }

    public void setLastClosedDailyBucketDate(Instant lastClosedDailyBucketDate) {
        this.lastClosedDailyBucketDate = lastClosedDailyBucketDate;
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
