package presidio.ade.domain.record.aggregated;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.AdeRecord;

import java.time.Instant;
import java.util.Map;

/**
 * Created by barak_schuster on 7/9/17.
 */
@Document
public class AdeAggregationRecord extends AdeRecord {
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
    public String getDataSource() {
        return null;
    }

    public Instant getEndInstant() {
        return endInstant;
    }

    public String getFeatureName() {
        return featureName;
    }

    public Double getFeatureValue() {
        return featureValue;
    }

    public String getFeatureBucketConfName() {
        return featureBucketConfName;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public AggregatedFeatureType getAggregatedFeatureType() {
        return aggregatedFeatureType;
    }
}
