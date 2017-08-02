package presidio.ade.domain.record.accumulator;

import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maria_dorohin on 7/26/17.
 */
public class AccumulatedAggregationFeatureRecord extends AdeContextualAggregatedRecord {

    private static final String ADE_ACCUMULATION_EVENT_TYPE_PREFIX = "accumulation_aggr_event";
    @Field
    private List<Double> aggregatedFeatureValues;


    public AccumulatedAggregationFeatureRecord(Instant startInstant, Instant endTime, String contextId, String featureName) {
        super(startInstant, endTime, contextId, featureName);
        this.aggregatedFeatureValues = new ArrayList<>();
    }

    /**
     *
     * @return list of aggregated feature values
     */
    public List<Double> getAggregatedFeatureValues() {
        return aggregatedFeatureValues;
    }

    /**
     * Set aggregated feature values
     * @param aggregatedFeatureValues
     */
    public void setAggregatedFeatureValues(List<Double> aggregatedFeatureValues) {
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
