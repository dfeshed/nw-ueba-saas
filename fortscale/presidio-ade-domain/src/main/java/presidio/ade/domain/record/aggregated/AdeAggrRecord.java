package presidio.ade.domain.record.aggregated;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.AdeRecord;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Created by barak_schuster on 7/9/17.
 */
@Document
public class AdeAggrRecord extends AdeRecord {
    private static final String ADE_EVENT_TYPE_PREFIX = "aggr";

    @Indexed
    private Instant endInstant;
    @Field
    private Map<String, Object> aggregatedFeatureInfo;
    @Field
    private String featureName;
    @Field
    private Double featureValue;
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
        this.featureName = aggregatedFeatureName;
        this.featureValue = aggregatedFeatureValue;
        this.bucketConfName = bucketConfName;
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

    public Instant getEndInstant() {
        return endInstant;
    }

    public Map<String, Object> getAggregatedFeatureInfo() {
        return aggregatedFeatureInfo;
    }

    public String getFeatureName() {
        return featureName;
    }

    public Double getFeatureValue() {
        return featureValue;
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
