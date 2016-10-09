package fortscale.accumulator.entityEvent.event;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * accumulated view of {@link fortscale.domain.core.EntityEvent}
 * to handle performance issues at large scale while building models.
 *
 * this object is thinner in manner of amount of fields,
 * and also accumulates several {@link fortscale.domain.core.EntityEvent#aggregated_feature_events#value}
 * into single list {@link this#aggregatedFeatureEventsValuesMap}
 *
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulatedEntityEvent {

    // --- field names as they appear in MongoDB ---
    public static final String ACCUMULATED_ENTITY_EVENT_FIELD_NAME_START_TIME = "start_time";
    private static final String ACCUMULATED_ENTITY_EVENT_FIELD_NAME_END_TIME = "end_time";
    private static final String ACCUMULATED_ENTITY_EVENT_FIELD_NAME_CONTEXT_ID = "contextId";
    private static final String ACCUMULATED_ENTITY_EVENT_FIELD_NAME_AGGREGATED_FEATURE_EVENTS_VALUES_MAP = "aggregated_feature_events_values_map";
    private static final String ACCUMULATED_ENTITY_EVENT_FIELD_NAME_CREATION_TIME = "creation_time";

    @Id
    private String id;

    @Field(ACCUMULATED_ENTITY_EVENT_FIELD_NAME_START_TIME)
    private Instant startTime;

    @Field(ACCUMULATED_ENTITY_EVENT_FIELD_NAME_END_TIME)
    private Instant endTime;

    @Field(ACCUMULATED_ENTITY_EVENT_FIELD_NAME_CONTEXT_ID)
    private String contextId;

    @Field(ACCUMULATED_ENTITY_EVENT_FIELD_NAME_AGGREGATED_FEATURE_EVENTS_VALUES_MAP)
    private Map<String,List<Double>> aggregatedFeatureEventsValuesMap;

    @Field(ACCUMULATED_ENTITY_EVENT_FIELD_NAME_CREATION_TIME)
    private Instant creationTime;

    /**
     * Default C'tor
     */
    public AccumulatedEntityEvent()
    {
        aggregatedFeatureEventsValuesMap = new HashMap<>();
    }

    public AccumulatedEntityEvent(Instant startTime, Instant endTime, String contextId, Instant creationTime)
    {
        this();
        this.startTime = startTime;
        this.endTime = endTime;
        this.contextId = contextId;
        this.creationTime = creationTime;
    }

    /**
     * C'tor
     * @param startTime
     * @param endTime
     * @param contextId
     * @param aggregatedFeatureEventsValuesMap
     * @param creationTime
     */
    public AccumulatedEntityEvent(Instant startTime, Instant endTime, String contextId, Map<String, List<Double>> aggregatedFeatureEventsValuesMap, Instant creationTime) {
        this(startTime,endTime,contextId,creationTime);
        this.aggregatedFeatureEventsValuesMap = aggregatedFeatureEventsValuesMap;
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

    public Map<String, List<Double>> getAggregatedFeatureEventsValuesMap() {
        return aggregatedFeatureEventsValuesMap;
    }

    public void setAggregatedFeatureEventsValuesMap(Map<String, List<Double>> aggregatedFeatureEventsValuesMap) {
        this.aggregatedFeatureEventsValuesMap = aggregatedFeatureEventsValuesMap;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }
}
