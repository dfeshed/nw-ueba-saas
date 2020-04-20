package presidio.ade.domain.record.aggregated;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.Validate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 * @see AggregatedFeatureType
 */
@Document
public class AdeAggregationRecord extends AdeContextualAggregatedRecord {
    public static final String ADE_AGGR_EVENT_TYPE_PREFIX = "aggr_event.";
    public static final String FEATURE_VALUE_FIELD_NAME = "featureValue";

    @Field
    private String featureName;
    @Field(FEATURE_VALUE_FIELD_NAME)
    private Double featureValue;
    @Field
    private String featureBucketConfName;
    @Field
    private Map<String, String> context;
    @Field
    private AggregatedFeatureType aggregatedFeatureType;

    public AdeAggregationRecord() {
        super();
    }

    public AdeAggregationRecord(Instant startInstant, Instant endInstant, String featureName, Double featureValue,
                                String featureBucketConfName, Map<String, String> context, AggregatedFeatureType aggregatedFeatureType) {
        super(startInstant, endInstant, buildContextId(context));
        this.featureValue = featureValue;
        this.featureBucketConfName = featureBucketConfName;
        this.context = context;
        this.aggregatedFeatureType = aggregatedFeatureType;
        this.featureName = featureName;
    }

    @Override
    public String getAdeEventType() {
        return getAdeEventType(featureName);
    }

    @Override
    public List<String> getDataSources() {
        return null;
    }

    /**
     * @return the aggregated value itself (i.e. the sum itself)
     */
    public Double getFeatureValue() {
        return featureValue;
    }

    /**
     * @return The bucket the aggregation relay upon. i.e. the contextual hourly bucket the aggregation relay upon
     */
    public String getFeatureBucketConfName() {
        return featureBucketConfName;
    }

    /**
     * @return context of the aggregation in field_name, field_value map
     */
    public Map<String, String> getContext() {
        return context;
    }

    /**
     * @return aggregation feature type
     */
    public AggregatedFeatureType getAggregatedFeatureType() {
        return aggregatedFeatureType;
    }

    /**
     * Set feature name
     *
     * @param featureName feature name
     */
    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    /**
     * @return name of the aggregated feature. i.e. sum_of_xxx_daily or highest_xxx_score_daily
     */
    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureValue(Double featureValue) {
        this.featureValue = featureValue;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static String getAdeEventType(String aggregationRecordName) {
        return ADE_AGGR_EVENT_TYPE_PREFIX + aggregationRecordName;
    }

    public static String getAggregationRecordName(String adeEventType) {
        Validate.isTrue(adeEventType.startsWith(ADE_AGGR_EVENT_TYPE_PREFIX),
                "ADE event type %s is not an aggregation record type.", adeEventType);
        return adeEventType.substring(ADE_AGGR_EVENT_TYPE_PREFIX.length());
    }
}
