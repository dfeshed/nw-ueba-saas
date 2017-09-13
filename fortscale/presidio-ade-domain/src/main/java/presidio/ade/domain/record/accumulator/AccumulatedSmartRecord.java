package presidio.ade.domain.record.accumulator;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;

import java.time.Instant;
import java.util.*;

/**
 * accumulated view of {@link presidio.ade.domain.record.aggregated.SmartRecord}
 * to handle performance issues at large scale while building models.
 */
@Document
@CompoundIndexes({
        @CompoundIndex(def = "{'startInstant': 1}"),
        @CompoundIndex(def = "{'startInstant': 1, 'contextId': 1}", unique = true)
})
public class AccumulatedSmartRecord extends AdeContextualAggregatedRecord {

    private static final String ADE_ACCUMULATION_SMART_TYPE_PREFIX = "accm_smart";

    // <featureName<smartHour, value | score>>
    private Map<String,Map<Integer,Double>> aggregatedFeatureEventsValuesMap;

    private Set<Integer> activityTime;

    @Transient
    private String featureName;

    public AccumulatedSmartRecord(){
        super();
    }

    public AccumulatedSmartRecord(Instant startInstant, Instant endInstant, String contextId, String featureName) {
        super(startInstant, endInstant, contextId);
        this.featureName = featureName;
        this.aggregatedFeatureEventsValuesMap = new HashMap<>();
        activityTime = new HashSet<>();
    }

    public Map<String,Map<Integer,Double>>  getAggregatedFeatureEventsValuesMap() {
        return aggregatedFeatureEventsValuesMap;
    }

    public void setAggregatedFeatureEventsValuesMap(Map<String,Map<Integer,Double>>  aggregatedFeatureEventsValuesMap) {
        this.aggregatedFeatureEventsValuesMap = aggregatedFeatureEventsValuesMap;
    }

    public Set<Integer> getActivityTime(){
        return activityTime;
    }

    public void setActivityTime(Set<Integer> activityTime){
        this.activityTime = activityTime;
    }

    @Override
    public String getAdeEventType() {
        return ADE_ACCUMULATION_SMART_TYPE_PREFIX + "." + getFeatureName();
    }

    @Override
    public List<String> getDataSources() {
        return null;
    }


    /**
     * Set feature name
     * @param featureName feature name
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
