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
public class AdeAggrRecord extends AdeRecord {
    @Indexed
    private Instant endInstant;
    @Field
    private Map<String, Object> aggregatedFeatureInfo;
    @Field
    private String aggregatedFeatureName;
    @Field
    private Double aggregatedFeatureValue;
    @Field
    private String bucketConfName;
    @Field
    private Map<String, String> context;
    @Field
    private AggregatedFeatureType aggregatedFeatureType;

    public AdeAggrRecord(Instant startInstant, Instant endInstant, Map<String, Object> aggregatedFeatureInfo, String aggregatedFeatureName, Double aggregatedFeatureValue, String bucketConfName, Map<String, String> context,AggregatedFeatureType aggregatedFeatureType) {
        super(startInstant);
        this.endInstant = endInstant;
        this.aggregatedFeatureInfo = aggregatedFeatureInfo;
        this.aggregatedFeatureName = aggregatedFeatureName;
        this.aggregatedFeatureValue = aggregatedFeatureValue;
        this.bucketConfName = bucketConfName;
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

    public Map<String, Object> getAggregatedFeatureInfo() {
        return aggregatedFeatureInfo;
    }

    public String getAggregatedFeatureName() {
        return aggregatedFeatureName;
    }

    public Double getAggregatedFeatureValue() {
        return aggregatedFeatureValue;
    }

    public String getBucketConfName() {
        return bucketConfName;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public AggregatedFeatureType getAggregatedFeatureType() {
        return aggregatedFeatureType;
    }
}
