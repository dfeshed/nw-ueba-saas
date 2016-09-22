package fortscale.domain.core.dao;

/**
 * Created by Amir Keren on 03/09/15.
 */
public class VpnLateralMovement {

	private String username;
	private String normalized_username;
	private String source_ip;
	private String normalized_src_machine;
	private String normalized_dst_machine;
	private String display_name;
	private String entity_id;
	private String data_source;
	private long event_time_utc;
	private long eventscore;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNormalized_username() {
		return normalized_username;
	}

	public void setNormalized_username(String normalized_username) {
		this.normalized_username = normalized_username;
	}

	public String getSource_ip() {
		return source_ip;
	}

	public void setSource_ip(String source_ip) {
		this.source_ip = source_ip;
	}

	public String getNormalized_src_machine() {
		return normalized_src_machine;
	}

	public void setNormalized_src_machine(String normalized_src_machine) {
		this.normalized_src_machine = normalized_src_machine;
	}

	public String getNormalized_dst_machine() {
		return normalized_dst_machine;
	}

	public void setNormalized_dst_machine(String normalized_dst_machine) {
		this.normalized_dst_machine = normalized_dst_machine;
	}

	public long getEvent_time_utc() {
		return event_time_utc;
	}

	public void setEvent_time_utc(long event_time_utc) {
		this.event_time_utc = event_time_utc;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public String getEntity_id() {
		return entity_id;
	}

	public void setEntity_id(String entity_id) {
		this.entity_id = entity_id;
	}

	public String getData_source() {
		return data_source;
	}

	public void setData_source(String data_source) {
		this.data_source = data_source;
	}

	public long getEventscore() {
		return eventscore;
	}

	public void setEventscore(long eventscore) {
		this.eventscore = eventscore;
	}

}