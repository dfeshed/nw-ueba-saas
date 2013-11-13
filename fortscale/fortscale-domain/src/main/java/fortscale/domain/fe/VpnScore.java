package fortscale.domain.fe;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({"eventTime","userName","sourceIp","localIp","status","eventScore","globalScore","timestamp"})
public class VpnScore {
	public static final Object impalaFieldsOrder[] = {VpnScore.USERNAME_FIELD_NAME,VpnScore.LOCAL_IP_FIELD_NAME,VpnScore.SOURCE_IP_FIELD_NAME,VpnScore.STATUS_FIELD_NAME,VpnScore.EVENT_SCORE_FIELD_NAME,VpnScore.GLOBAL_SCORE_FIELD_NAME,VpnScore.TIMESTAMP_FIELD_NAME,VpnScore.EVENT_TIME_FIELD_NAME};
	public static final String implaValueTypeOrder = String.format("%s string, %s string, %s string, %s string, %s double, %s double, %s bigint, %s timestamp", impalaFieldsOrder);
	
	public static final String TABLE_NAME = "vpndatares";
	
	
	public static final String EVENT_TIME_FIELD_NAME = "date_time";
	public static final String USERNAME_FIELD_NAME = "username";
	public static final String SOURCE_IP_FIELD_NAME = "source_ip";
	public static final String LOCAL_IP_FIELD_NAME = "local_ip";
	public static final String STATUS_FIELD_NAME = "status";
	
	public static final String EVENT_TIME_SCORE_FIELD_NAME = "date_timescore";
	public static final String USERNAME_SCORE_FIELD_NAME = "usernamescore";
	public static final String SOURCE_IP_SCORE_FIELD_NAME = "source_ipscore";
	public static final String LOCAL_IP_SCORE_FIELD_NAME = "local_ipscore";
	public static final String STATUS_SCORE_FIELD_NAME = "statusscore";
	
	public static final String EVENT_SCORE_FIELD_NAME = "eventscore";
	public static final String GLOBAL_SCORE_FIELD_NAME = "globalscore";
	
	
	
	public static final String TIMESTAMP_FIELD_NAME = "runtime";
	
	
	
	
	private Date eventTime;
	private String userName;
	private String sourceIp;
	private String localIp;
	private String status;
	
	private double eventTimeScore;
	private double userNameScore;
	private double sourceIpScore;
	private double localIpScore;
	private double statusScore;
	
	private double eventScore;
	private double globalScore;
	
	
	
	private Date timestamp;
	
	
	public Date getEventTime() {
		return eventTime;
	}
	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSourceIp() {
		return sourceIp;
	}
	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}
	public String getLocalIp() {
		return localIp;
	}
	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getEventScore() {
		return eventScore;
	}
	public void setEventScore(double eventScore) {
		this.eventScore = eventScore;
	}
	public double getGlobalScore() {
		return globalScore;
	}
	public void setGlobalScore(double globalScore) {
		this.globalScore = globalScore;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public double getEventTimeScore() {
		return eventTimeScore;
	}
	public void setEventTimeScore(double eventTimeScore) {
		this.eventTimeScore = eventTimeScore;
	}
	public double getUserNameScore() {
		return userNameScore;
	}
	public void setUserNameScore(double userNameScore) {
		this.userNameScore = userNameScore;
	}
	public double getSourceIpScore() {
		return sourceIpScore;
	}
	public void setSourceIpScore(double sourceIpScore) {
		this.sourceIpScore = sourceIpScore;
	}
	public double getLocalIpScore() {
		return localIpScore;
	}
	public void setLocalIpScore(double localIpScore) {
		this.localIpScore = localIpScore;
	}
	public double getStatusScore() {
		return statusScore;
	}
	public void setStatusScore(double statusScore) {
		this.statusScore = statusScore;
	}
	
	
	
}
