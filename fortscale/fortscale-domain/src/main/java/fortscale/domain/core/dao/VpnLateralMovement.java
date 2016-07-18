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
	private long date_time_unix;

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

	public long getDate_time_unix() {
		return date_time_unix;
	}

	public void setDate_time_unix(long date_time_unix) {
		this.date_time_unix = date_time_unix;
	}

}