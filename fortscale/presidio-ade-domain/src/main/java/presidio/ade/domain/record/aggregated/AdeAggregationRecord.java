package presidio.ade.domain.record.aggregated;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Created by barak_schuster on 7/9/17.
 * @see AggregatedFeatureType
 */
@Document
@CompoundIndexes({
        @CompoundIndex(name = "context_pagination", def = "{'contextId': -1, 'startInstant': -1}"),
        @CompoundIndex(name = "value_threshold_query", def = "{'contextId': -1, 'startInstant': -1, 'featureValue': -1}")
})
public class AdeAggregationRecord extends AdeContextualAggregatedRecord {
    public static final String ADE_AGGR_EVENT_TYPE_PREFIX = "aggr_event";
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

    public AdeAggregationRecord(Instant startInstant, Instant endInstant, String featureName, Double featureValue, String featureBucketConfName, Map<String, String> context, AggregatedFeatureType aggregatedFeatureType) {
        super(startInstant, endInstant, getAggregatedFeatureContextId(context));
        this.featureValue = featureValue;
        this.featureBucketConfName = featureBucketConfName;
        this.context = context;
        this.aggregatedFeatureType = aggregatedFeatureType;
        this.featureName = featureName;
    }

    @Override
    public String getAdeEventType() {
        return ADE_AGGR_EVENT_TYPE_PREFIX + "." + getFeatureName();
    }

    @Override
    public List<String> getDataSources() {
        return null;
    }

    /**
     *
     * @return the aggregated value itself (i.e. the sum itself)
     */
    public Double getFeatureValue() {
        return featureValue;
    }

    /**
     *
     * @return The bucket the aggregation relay upon. i.e. the contextual hourly bucket the aggregation relay upon
     */
    public String getFeatureBucketConfName() {
        return featureBucketConfName;
    }

    /**
     *
     * @return context of the aggregation in field_name, field_value map
     */
    public Map<String, String> getContext() {
        return context;
    }

    /**
     *
     * @return aggregation feature type
     */
    public AggregatedFeatureType getAggregatedFeatureType() {
        return aggregatedFeatureType;
    }

    /**
     * Set feature name
     * @param featureName
     */
    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    /**
     *
     * @return name of the aggregated feature. i.e. sum_of_xxx_daily or highest_xxx_score_daily
     */
    public String getFeatureName() {
        return featureName;
    }
}
