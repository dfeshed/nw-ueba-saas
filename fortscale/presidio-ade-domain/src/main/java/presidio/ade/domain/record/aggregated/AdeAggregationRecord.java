package presidio.ade.domain.record.aggregated;

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
public class AdeAggregationRecord extends AdeContextualAggregatedRecord {
    private static final String ADE_EVENT_TYPE_PREFIX = "aggr_event";

    @Field
    private String featureName;
    @Field
    private Double featureValue;
    @Field
    private String featureBucketConfName;
    @Field
    private Map<String, String> context;
    @Field
    private AggregatedFeatureType aggregatedFeatureType;

    public AdeAggregationRecord(Instant startInstant, Instant endInstant, String featureName, Double featureValue, String featureBucketConfName, Map<String,String> context, AggregatedFeatureType aggregatedFeatureType) {
        super(startInstant, endInstant, getAggregatedFeatureContextId(context), featureName);
        this.featureValue = featureValue;
        this.featureBucketConfName = featureBucketConfName;
        this.context = context;
        this.aggregatedFeatureType = aggregatedFeatureType;
    }

    @Override
    public String getAdeEventType() {
        return ADE_EVENT_TYPE_PREFIX + "." + getFeatureName();
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
}
