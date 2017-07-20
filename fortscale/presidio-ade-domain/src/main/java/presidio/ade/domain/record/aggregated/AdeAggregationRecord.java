package presidio.ade.domain.record.aggregated;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.AdeRecord;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 7/9/17.
 * @see AggregatedFeatureType
 */
@Document
public class AdeAggregationRecord extends AdeRecord {
    private static final String ADE_EVENT_TYPE_PREFIX = "aggr_event";

    @Indexed
    private Instant endInstant;

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

    public AdeAggregationRecord(Instant startInstant, Instant endInstant, String featureName, Double featureValue, String featureBucketConfName, Map<String, String> context, AggregatedFeatureType aggregatedFeatureType) {
        super(startInstant);
        this.endInstant = endInstant;
        this.featureName = featureName;
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
     * @return aggregation accumulates records between between a time range. it consist of a start and end. this is the end...
     */
    public Instant getEndInstant() {
        return endInstant;
    }

    /**
     *
     * @return name of the aggregated feature. i.e. sum_of_xxx_daily or highest_xxx_score_daily
     */
    public String getFeatureName() {
        return featureName;
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
