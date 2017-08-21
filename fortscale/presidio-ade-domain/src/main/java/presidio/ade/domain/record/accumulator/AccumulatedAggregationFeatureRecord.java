package presidio.ade.domain.record.accumulator;

import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maria_dorohin on 7/26/17.
 */
public class AccumulatedAggregationFeatureRecord extends AdeContextualAggregatedRecord {

    private static final String ADE_ACCUMULATION_EVENT_TYPE_PREFIX = "accumulation_aggr_event";
    @Field
    private Map<Integer,Double> aggregatedFeatureValues;

    public AccumulatedAggregationFeatureRecord() {
        super();
    }

    public AccumulatedAggregationFeatureRecord(Instant startInstant, Instant endInstant, String contextId, String featureName) {
        super(startInstant, endInstant, contextId, featureName);
        this.aggregatedFeatureValues = new HashMap();
    }

    /**
     * @return list of aggregated feature values
     */
    public Collection<Double> getAggregatedFeatureValuesAsList() {
        return aggregatedFeatureValues.values();
    }

    /**
     *
     * @return map of aggregated feature values.
     * the key is the floored start hour integer, the value is the actual aggregated feature value
     */
    public Map<Integer, Double> getAggregatedFeatureValues() {
        return aggregatedFeatureValues;
    }

    /**
     * Set aggregated feature values
     *
     * @param aggregatedFeatureValues
     */
    public void setAggregatedFeatureValues(Map<Integer,Double> aggregatedFeatureValues) {
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
}
