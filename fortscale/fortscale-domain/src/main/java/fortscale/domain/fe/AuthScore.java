package fortscale.domain.fe;

import java.util.Date;

public class AuthScore {
	public static final String TABLE_NAME = "authenticationscores";
	public static final String USERNAME_FIELD_NAME = "userid";
	public static final String TARGET_ID_FIELD_NAME = "targetid";
	public static final String SOURCE_IP_FIELD_NAME = "sourceip";
	public static final String ERROR_CODE_FIELD_NAME = "errorcode";
	public static final String EVENT_SCORE_FIELD_NAME = "eventscore";
	public static final String GLOBAL_SCORE_FIELD_NAME = "globalscore";
	public static final String TIMESTAMP_FIELD_NAME = "runtime";
	public static final String EVENT_TIME_FIELD_NAME = "time";
	
	
	private String userName;
	private String targetId;
	private String sourceIp;
	private String errorCode;
	private double eventScore;
	private double globalScore;
	private Date timestamp;
	private Date eventTime;
	
	
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public String getSourceIp() {
		return sourceIp;
	}
	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
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
	public Date getEventTime() {
		return eventTime;
	}
	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}
}
