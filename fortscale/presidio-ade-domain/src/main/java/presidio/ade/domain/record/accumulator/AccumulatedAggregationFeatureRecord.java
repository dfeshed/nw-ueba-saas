package presidio.ade.domain.record.accumulator;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maria Dorohin
 */
@Document
public class AccumulatedAggregationFeatureRecord extends AdeContextualAggregatedRecord {
    private static final String ADE_ACCUMULATION_EVENT_TYPE_PREFIX = "accumulation_aggr_event";

    @Field
    private Map<Integer, Double> aggregatedFeatureValues;

    @Transient
    private String featureName;

    public AccumulatedAggregationFeatureRecord() {
        super();
    }

    public AccumulatedAggregationFeatureRecord(Instant startInstant, Instant endInstant, String contextId, String featureName) {
        super(startInstant, endInstant, contextId);
        this.featureName = featureName;
        this.aggregatedFeatureValues = new HashMap<>();
    }

    /**
     * @return list of aggregated feature values
     */
    public Collection<Double> getAggregatedFeatureValuesAsList() {
        return aggregatedFeatureValues.values();
    }

    /**
     * @return map of aggregated feature values.
     * the key is the floored start hour integer, the value is the actual aggregated feature value
     */
    public Map<Integer, Double> getAggregatedFeatureValues() {
        return aggregatedFeatureValues;
    }

    /**
     * Set aggregated feature values
     *
     * @param aggregatedFeatureValues aggregated feature values
     */
    public void setAggregatedFeatureValues(Map<Integer, Double> aggregatedFeatureValues) {
        this.aggregatedFeatureValues = aggregatedFeatureValues;
    }

    @Override
    public String getAdeEventType() {
        return ADE_ACCUMULATION_EVENT_TYPE_PREFIX + "." + getFeatureName();
    }

    @Override
    public List<String> getDataSources() {
        return null;
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
}
