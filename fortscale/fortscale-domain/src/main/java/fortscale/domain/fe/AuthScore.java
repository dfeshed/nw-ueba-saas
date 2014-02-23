package fortscale.domain.fe;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import fortscale.utils.impala.ImpalaDateTime;


@JsonPropertyOrder({AuthScore.USERNAME_FIELD_NAME,AuthScore.TARGET_ID_FIELD_NAME,AuthScore.SOURCE_IP_FIELD_NAME,AuthScore.ERROR_CODE_FIELD_NAME,AuthScore.EVENT_SCORE_FIELD_NAME,AuthScore.GLOBAL_SCORE_FIELD_NAME,AuthScore.TIMESTAMP_FIELD_NAME,AuthScore.EVENT_TIME_FIELD_NAME})
public class AuthScore {
	public static final Object jsonOrder[] = {AuthScore.USERNAME_FIELD_NAME,AuthScore.TARGET_ID_FIELD_NAME,AuthScore.SOURCE_IP_FIELD_NAME,AuthScore.ERROR_CODE_FIELD_NAME,AuthScore.EVENT_SCORE_FIELD_NAME,AuthScore.GLOBAL_SCORE_FIELD_NAME,AuthScore.TIMESTAMP_FIELD_NAME,AuthScore.EVENT_TIME_FIELD_NAME};
	public static final String implaValueTypeOrder = String.format("%s string, %s string, %s string, %s string, %s double, %s double, %s bigint, %s timestamp", jsonOrder);
	
//	public static final String TABLE_NAME = "authenticationscores";
	
	
	public static final String USERNAME_FIELD_NAME = "userid";
	public static final String TARGET_ID_FIELD_NAME = "targetid";
	public static final String SOURCE_IP_FIELD_NAME = "sourceip";
	public static final String ERROR_CODE_FIELD_NAME = "errorcode";
	public static final String EVENT_TIME_FIELD_NAME = "time";
	
	public static final String USERNAME_SCORE_FIELD_NAME = "useridscore";
	public static final String TARGET_ID_SCORE_FIELD_NAME = "targetidscore";
	public static final String SOURCE_IP_SCORE_FIELD_NAME = "sourceipscore";
	public static final String ERROR_CODE_SCORE_FIELD_NAME = "errorcodescore";
	public static final String EVENT_TIME_SCORE_FIELD_NAME = "timescore";
	
	
	public static final String EVENT_SCORE_FIELD_NAME = "eventscore";
	public static final String GLOBAL_SCORE_FIELD_NAME = "globalscore";
	
	
	
	public static final String TIMESTAMP_FIELD_NAME = "runtime";
		
	
	private String userName;
	private String targetId;
	private String sourceIp;
	private Date eventTime;
	
	private double userNameScore;
	private double targetIdScore;
	private double sourceIpScore;
	private double eventTimeScore;
	
	private double eventScore;
	private double globalScore;
	private Date timestamp;
	
	private String status;
	
	private Map<String, Object> allFields = new HashMap<String, Object>();
	
	
	
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=ImpalaDateTime.DATE_FORMAT)
	public Date getEventTime() {
		return eventTime;
	}
	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}
	public double getUserNameScore() {
		return userNameScore;
	}
	public void setUserNameScore(double userNameScore) {
		this.userNameScore = userNameScore;
	}
	public double getTargetIdScore() {
		return targetIdScore;
	}
	public void setTargetIdScore(double targetIdScore) {
		this.targetIdScore = targetIdScore;
	}
	public double getSourceIpScore() {
		return sourceIpScore;
	}
	public void setSourceIpScore(double sourceIpScore) {
		this.sourceIpScore = sourceIpScore;
	}
	public double getEventTimeScore() {
		return eventTimeScore;
	}
	public void setEventTimeScore(double eventTimeScore) {
		this.eventTimeScore = eventTimeScore;
	}
	public Map<String, Object> getAllFields() {
		return allFields;
	}
	public void setAllFields(Map<String, Object> allFields) {
		this.allFields = allFields;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
