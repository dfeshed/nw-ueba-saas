package fortscale.accumulator.entityEvent.event;

import fortscale.domain.SMART.EntityEvent;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * accumulated view of {@link EntityEvent}
 * to handle performance issues at large scale while building models.
 *
 * this object is thinner in manner of amount of fields,
 * and also accumulates several {@link EntityEvent#aggregated_feature_events#value}
 * into single list {@link this#aggregated_feature_events_values_map}
 *
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulatedEntityEvent {

    // --- field names as they appear in MongoDB ---
    public static final String ACCUMULATED_ENTITY_EVENT_FIELD_NAME_START_TIME = "start_time";
    public static final String ACCUMULATED_ENTITY_EVENT_FIELD_NAME_END_TIME = "end_time";
    public static final String ACCUMULATED_ENTITY_EVENT_FIELD_NAME_CREATION_TIME = "creation_time";
    public static final String ACCUMULATED_ENTITY_EVENT_FIELD_NAME_CONTEXT_ID = "contextId";

    @Id
    private String id;


    private Instant start_time;

    private Instant end_time;

    private String contextId;

    // <bucket-featurename<hour, value>>
    private Map<String,Map<Integer,Double>> aggregated_feature_events_values_map;

    private Set<Integer> activityTime;

    private Instant creation_time;

    /**
     * Default C'tor
     */
    public AccumulatedEntityEvent()
    {
        aggregated_feature_events_values_map = new HashMap<>();
        activityTime = new HashSet<>();
    }

    public AccumulatedEntityEvent(Instant start_time, Instant end_time, String contextId, Instant creation_time)
    {
        this();
        this.start_time = start_time;
        this.end_time = end_time;
        this.contextId = contextId;
        this.creation_time = creation_time;
    }

    /**
     * C'tor
     * @param start_time
     * @param end_time
     * @param contextId
     * @param aggregated_feature_events_values_map
     * @param creation_time
     */
    public AccumulatedEntityEvent(Instant start_time, Instant end_time, String contextId, Map<String,Map<Integer,Double>>  aggregated_feature_events_values_map, Instant creation_time, Set<Integer> activityTime) {
        this(start_time, end_time,contextId, creation_time);
        this.aggregated_feature_events_values_map = aggregated_feature_events_values_map;
        this.activityTime = activityTime;
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

    public Map<String,Map<Integer,Double>>  getAggregated_feature_events_values_map() {
        return aggregated_feature_events_values_map;
    }

    public void setAggregated_feature_events_values_map(Map<String,Map<Integer,Double>>  aggregated_feature_events_values_map) {
        this.aggregated_feature_events_values_map = aggregated_feature_events_values_map;
    }

    public Set<Integer> getActivityTime(){
        return activityTime;
    }

    public void setActivityTime(Set<Integer> activityTime){
        this.activityTime = activityTime;
    }

    public Instant getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(Instant creation_time) {
        this.creation_time = creation_time;
    }
}
