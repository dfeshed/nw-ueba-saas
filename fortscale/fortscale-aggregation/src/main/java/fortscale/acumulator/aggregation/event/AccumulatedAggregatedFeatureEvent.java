package fortscale.acumulator.aggregation.event;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

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
    private static final String ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_START_TIME = "start_time";
    private static final String ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_END_TIME = "end_time";
    private static final String ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_AGGREGATED_FEATURE_VALUES = "aggregated_feature_values";
    private static final String ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_CONTEXT_ID = "contextId";
    private static final String ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_CREATION_TIME = "creation_time";

    @Id
    private String id;

    @Field(ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_START_TIME)
    private Instant startTime;

    @Field(ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_END_TIME)
    private Instant endTime;

    @Field(ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_CONTEXT_ID)
    private String contextId;

    @Field(ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_AGGREGATED_FEATURE_VALUES)
    private List<Double> aggregatedFeatureValues;

    @Field(ACCUMULATED_AGGREGATED_FEATURE_EVENT_FIELD_NAME_CREATION_TIME)
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
     * @param startTime
     * @param endTime
     * @param contextId
     * @param aggregatedFeatureValues
     * @param creationTime
     */
    public AccumulatedAggregatedFeatureEvent(Instant startTime, Instant endTime, String contextId, List<Double> aggregatedFeatureValues, Instant creationTime) {
        this(startTime,endTime,contextId,creationTime);
        this.aggregatedFeatureValues = aggregatedFeatureValues;
    }

    public AccumulatedAggregatedFeatureEvent(Instant startTime, Instant endTime, String contextId, Instant creationTime) {
        this();
        this.startTime = startTime;
        this.endTime = endTime;
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

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
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
