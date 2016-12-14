package fortscale.aggregation.feature.bucket.repository.state;

import fortscale.aggregation.feature.bucket.repository.FeatureBucketMetadata;
import fortscale.domain.core.AbstractDocument;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Date;

@Document(collection = FeatureBucketMetadata.COLLECTION_NAME)
public class FeatureBucketState extends AbstractDocument {
    public static final String COLLECTION_NAME = "FeatureBucketState";

    public static final String STATE_FIELD_NAME = "state";
    public static final String STATE_TYPE_FIELD_NAME = "stateType";

    @Field(STATE_FIELD_NAME)
    private AggregationFeatureStateDate aggregationFeatureStateDate;

    @Field(STATE_TYPE_FIELD_NAME)
    private StateType stateType;

    public FeatureBucketState() {
    }

    public FeatureBucketState(AggregationFeatureStateDate aggregationFeatureStateDate, StateType stateType) {
        this.aggregationFeatureStateDate = aggregationFeatureStateDate;
        this.stateType = stateType;
    }

    public FeatureBucketState(Instant date, StateType stateType) {
        this.stateType = stateType;
        this.aggregationFeatureStateDate = new AggregationFeatureStateDate(date);
    }

    public AggregationFeatureStateDate getAggregationFeatureStateDate() {
        return aggregationFeatureStateDate;
    }

    public void setAggregationFeatureStateDate(AggregationFeatureStateDate aggregationFeatureStateDate) {
        this.aggregationFeatureStateDate = aggregationFeatureStateDate;
    }

    public StateType getStateType() {
        return stateType;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

    public void updateAggregationStateDate(Instant date){
        this.aggregationFeatureStateDate.setDate(date);
        this.aggregationFeatureStateDate.setModifiedAt(new Date());
    }

    public class AggregationFeatureStateDate {
        public static final String DATE_FIELD = "dateField";
        public static final String MODIFIED_AT_FIELD = "modifiedAt";

        @Field(DATE_FIELD)
        private Instant date;

        @Field(MODIFIED_AT_FIELD)
        private Date modifiedAt;

        public AggregationFeatureStateDate(Instant date) {
            this.date = date;
            this.modifiedAt = new Date();

        }

        public Instant getDate() {
            return date;
        }

        public void setDate(Instant date) {
            this.date = date;
        }

        public Date getModifiedAt() {
            return modifiedAt;
        }

        public void setModifiedAt(Date modifiedAt) {
            this.modifiedAt = modifiedAt;
        }
    }

    public enum StateType{
        LAST_SYNC_DATE
    }
}
