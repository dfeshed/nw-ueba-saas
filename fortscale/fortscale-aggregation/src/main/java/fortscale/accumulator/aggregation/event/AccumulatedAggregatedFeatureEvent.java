package fortscale.accumulator.aggregation.event;

import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * accumulated view of {@link fortscale.aggregation.feature.event.AggrEvent}
 * to handle performance issues at large scale while building models.
 *
 * this object is thinner in manner of amount of fields,
 * and also accumulates several {@link fortscale.aggregation.feature.event.AggrEvent#aggregatedFeatureValue} into single list {@link this#aggregatedFeatureValues}
 *
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulatedAggregatedFeatureEvent {

    // --- field names as they appear in MongoDB ---
    public static final String ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_START_TIME = "start_time";
    public static final String ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_END_TIME = "end_time";
    public static final String ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_CONTEXT_ID = "contextId";
    public static final String ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_CREATION_TIME = "creationTime";

    @Id
    private String id;

    private Instant start_time;

    private Instant end_time;

    private String contextId;

    private List<Double> aggregatedFeatureValues;

    private Instant creationTime;

    /**
     * Default C'tor
     */
    public AccumulatedAggregatedFeatureEvent()
    {
        aggregatedFeatureValues = new ArrayList<>();
    }

    /**
     * C'tor
     * @param start_time
     * @param end_time
     * @param contextId
     * @param aggregatedFeatureValues
     * @param creationTime
     */
    public AccumulatedAggregatedFeatureEvent(Instant start_time, Instant end_time, String contextId, List<Double> aggregatedFeatureValues, Instant creationTime) {
        this(start_time, end_time,contextId,creationTime);
        this.aggregatedFeatureValues = aggregatedFeatureValues;
    }

    public AccumulatedAggregatedFeatureEvent(Instant start_time, Instant end_time, String contextId, Instant creationTime) {
        this();
        this.start_time = start_time;
        this.end_time = end_time;
        this.contextId = contextId;
        this.creationTime = creationTime;
    }

    // --- Getters/setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getStart_time() {
        return start_time;
    }

    public void setStart_time(Instant start_time) {
        this.start_time = start_time;
    }

    public Instant getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Instant end_time) {
        this.end_time = end_time;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public List<Double> getAggregatedFeatureValues() {
        return aggregatedFeatureValues;
    }

    public void setAggregatedFeatureValues(List<Double> aggregatedFeatureValues) {
        this.aggregatedFeatureValues = aggregatedFeatureValues;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }
}
