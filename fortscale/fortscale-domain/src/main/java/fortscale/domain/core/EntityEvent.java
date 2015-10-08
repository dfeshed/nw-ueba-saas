package fortscale.domain.core;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This is the bean of EntityEvent
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.NONE, getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility= JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class EntityEvent extends AbstractDocument implements Serializable {

	public static final String ENTITY_EVENT_START_TIME_UNIX_FILED_NAME = "start_time_unix";
	public static final String ENTITY_EVENT_VALUE_FILED_NAME = "entity_event_value";
	public static final String ENTITY_EVENT_SCORE_FILED_NAME = "score";
	public static final String ENTITY_EVENT_BASE_SCORE_FILED_NAME = "base_score";
	public static final String ENTITY_EVENT_CONTEXT_FILED_NAME = "context";
	public static final String ENTITY_EVENT_CONTEXT_ID_FILED_NAME = "contextId";
	public static final String ENTITY_EVENT_END_TIME_UNIX_FILED_NAME = "end_time_unix";
	public static final String ENTITY_EVENT_CREATION_EPOCHTIME_FILED_NAME = "creation_epochtime";
	public static final String ENTITY_EVENT_TYPE_FILED_NAME = "entity_event_type";
	public static final String ENTITY_EVENT_DATE_TIME_UNIX_FILED_NAME = "date_time_unix";
	public static final String ENTITY_EVENT_AGGREGATED_FEATURE_EVENTS_FILED_NAME = "aggregated_feature_events";

	private static final long serialVersionUID = -8514041678913795872L;

	@Field(ENTITY_EVENT_START_TIME_UNIX_FILED_NAME)
	private long start_time_unix;
	@Field(ENTITY_EVENT_VALUE_FILED_NAME)
	private double entity_event_value;
	@Field(ENTITY_EVENT_SCORE_FILED_NAME)
	private double score;
	@Field(ENTITY_EVENT_BASE_SCORE_FILED_NAME)
	private double base_score;
	@Field(ENTITY_EVENT_CONTEXT_FILED_NAME)
	private Map<String, String> context;
	@Field(ENTITY_EVENT_CONTEXT_ID_FILED_NAME)
	private String contextId;
	@Field(ENTITY_EVENT_END_TIME_UNIX_FILED_NAME)
	private Date end_time_unix;
	@Field(ENTITY_EVENT_CREATION_EPOCHTIME_FILED_NAME)
	private long creation_epochtime;
	@Field(ENTITY_EVENT_TYPE_FILED_NAME)
	private String entity_event_type;
	@Field(ENTITY_EVENT_DATE_TIME_UNIX_FILED_NAME)
	private long date_time_unix;
	@Field(ENTITY_EVENT_AGGREGATED_FEATURE_EVENTS_FILED_NAME)
	private List<JSONObject> aggregated_feature_events;


	public EntityEvent() {}

	public EntityEvent(long start_time_unix, double entity_event_value, double score, Map<String, String> context, String contextId, long end_time_unix, long creation_epochtime, String entity_event_type, long date_time_unix, List<JSONObject> aggregated_feature_events) {
		this.start_time_unix = start_time_unix;
		this.entity_event_value = entity_event_value;
		this.score = score;
		this.context = context;
		this.contextId = contextId;
		this.end_time_unix = new Date(TimestampUtils.convertToMilliSeconds(end_time_unix));
		this.creation_epochtime = creation_epochtime;
		this.entity_event_type = entity_event_type;
		this.date_time_unix = date_time_unix;
		this.aggregated_feature_events = aggregated_feature_events;
	}

	public static EntityEvent buildEntityEvent(JSONObject event) throws IOException, JsonParseException, JsonMappingException {
		return new ObjectMapper().readValue(event.toJSONString(), EntityEvent.class);
	}

	public long getStart_time_unix() {
		return start_time_unix;
	}

	public void setStart_time_unix(long start_time_unix) {
		this.start_time_unix = start_time_unix;
	}

	public double getEntity_event_value() {
		return entity_event_value;
	}

	public void setEntity_event_value(double entity_event_value) {
		this.entity_event_value = entity_event_value;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getBase_score() {
		return base_score;
	}

	public void setBase_score(double base_score) {
		this.base_score = base_score;
	}

	public Map<String, String> getContext() {
		return context;
	}

	public void setContext(Map<String, String> context) {
		this.context = context;
	}

	public long getEnd_time_unix() {
		return TimestampUtils.convertToSeconds(end_time_unix.getTime());
	}

	public void setEnd_time_unix(long end_time_unix_sec) {
		this.end_time_unix = new Date(TimestampUtils.convertToMilliSeconds(end_time_unix_sec));
	}

	public long getCreation_epochtime() {
		return creation_epochtime;
	}

	public void setCreation_epochtime(long creation_epochtime) {
		this.creation_epochtime = creation_epochtime;
	}

	public String getEntity_event_type() {
		return entity_event_type;
	}

	public void setEntity_event_type(String entity_event_type) {
		this.entity_event_type = entity_event_type;
	}

	public long getDate_time_unix() {
		return date_time_unix;
	}

	public void setDate_time_unix(long date_time_unix) {
		this.date_time_unix = date_time_unix;
	}

	public List<JSONObject> getAggregated_feature_events() {
		return aggregated_feature_events;
	}

	public void setAggregated_feature_events(List<JSONObject> aggregated_feature_events) {
		this.aggregated_feature_events = aggregated_feature_events;
	}
	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}


}
