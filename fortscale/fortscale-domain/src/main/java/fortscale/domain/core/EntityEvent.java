package fortscale.domain.core;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.minidev.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This is the bean of EntityEvent
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityEvent extends AbstractDocument implements Serializable {



	private static final long serialVersionUID = -8514041678913795872L;

	private long start_time_unix;
	private double entity_event_value;
	private String event_type;
	private double score;
	private Map<String, String> context;
	private String contextId;
	private long end_time_unix;
	private long creation_epochtime;
	private String entity_event_type;
	private long date_time_unix;
	private List<JSONObject> aggregated_feature_events;


	public EntityEvent() {}

	public EntityEvent(long start_time_unix, double entity_event_value, String event_type, double score, Map<String, String> context, String contextId, long end_time_unix, long creation_epochtime, String entity_event_type, long date_time_unix, List<JSONObject> aggregated_feature_events) {
		this.start_time_unix = start_time_unix;
		this.entity_event_value = entity_event_value;
		this.event_type = event_type;
		this.score = score;
		this.context = context;
		this.contextId = contextId;
		this.end_time_unix = end_time_unix;
		this.creation_epochtime = creation_epochtime;
		this.entity_event_type = entity_event_type;
		this.date_time_unix = date_time_unix;
		this.aggregated_feature_events = aggregated_feature_events;
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

	public String getEvent_type() {
		return event_type;
	}

	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Map<String, String> getContext() {
		return context;
	}

	public void setContext(Map<String, String> context) {
		this.context = context;
	}

	public long getEnd_time_unix() {
		return end_time_unix;
	}

	public void setEnd_time_unix(long end_time_unix) {
		this.end_time_unix = end_time_unix;
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
