package fortscale.domain.fe;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class VpnScore {	
//	public static final String EVENT_TIME_FIELD_NAME = "date_time";
//	public static final String USERNAME_FIELD_NAME = "username";
//	public static final String SOURCE_IP_FIELD_NAME = "source_ip";
//	public static final String LOCAL_IP_FIELD_NAME = "local_ip";
//	public static final String COUNTRY_FIELD_NAME = "country";
//	
//	public static final String EVENT_TIME_SCORE_FIELD_NAME = "date_timescore";
//	public static final String USERNAME_SCORE_FIELD_NAME = "usernamescore";
//	public static final String SOURCE_IP_SCORE_FIELD_NAME = "source_ipscore";
//	public static final String STATUS_SCORE_FIELD_NAME = "statusscore";
//	public static final String COUNTRY_SCORE_FIELD_NAME = "countryscore";
//	
//	public static final String EVENT_SCORE_FIELD_NAME = "eventscore";
//	public static final String GLOBAL_SCORE_FIELD_NAME = "globalscore";
//	
//	
//	
	public static final String TIMESTAMP_FIELD_NAME = "runtime";
	
	
	
	private String normalized_username;
	private Date date_time;
	private String username;
	private String source_ip;
	private String local_ip;
	private String status;
	private String country;
	private String region;
	private String city;
	private String isp;
	private String ipusage;
	
	private Double date_timeScore;
	private Double countryScore;
	private Double regionScore;
	private Double cityScore;
	private Double ispScore;
	private Double ipusageScore;
	
	private Double eventScore;
	private Double globalScore;
	
	
	
	private Integer runtime;
	
	private Map<String, Object> allFields = new HashMap<String, Object>();
	
	
	
	
	
	public String getNormalized_username() {
		return normalized_username;
	}
	public void setNormalized_username(String normalizedUsername) {
		this.normalized_username = normalizedUsername;
	}
	public Date getDate_time() {
		return date_time;
	}
	public void setDate_time(Date eventTime) {
		this.date_time = eventTime;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String userName) {
		this.username = userName;
	}
	public String getSource_ip() {
		return source_ip;
	}
	public void setSource_ip(String sourceIp) {
		this.source_ip = sourceIp;
	}
	public String getLocal_ip() {
		return local_ip;
	}
	public void setLocal_ip(String localIp) {
		this.local_ip = localIp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Double getEventScore() {
		return eventScore;
	}
	public void setEventScore(Double eventScore) {
		this.eventScore = eventScore;
	}
	public Double getGlobalScore() {
		return globalScore;
	}
	public void setGlobalScore(Double globalScore) {
		this.globalScore = globalScore;
	}
	public Integer getRuntime() {
		return runtime;
	}
	public void setRuntime(Integer runtime) {
		this.runtime = runtime;
	}
	public Double getDate_timeScore() {
		return date_timeScore;
	}
	public void setDate_timeScore(Double eventTimeScore) {
		this.date_timeScore = eventTimeScore;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Double getCountryScore() {
		return countryScore;
	}
	public void setCountryScore(Double countryScore) {
		this.countryScore = countryScore;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getIsp() {
		return isp;
	}
	public void setIsp(String isp) {
		this.isp = isp;
	}
	public String getIpusage() {
		return ipusage;
	}
	public void setIpusage(String ipusage) {
		this.ipusage = ipusage;
	}
	public Double getRegionScore() {
		return regionScore;
	}
	public void setRegionScore(Double regionScore) {
		this.regionScore = regionScore;
	}
	public Double getCityScore() {
		return cityScore;
	}
	public void setCityScore(Double cityScore) {
		this.cityScore = cityScore;
	}
	public Double getIspScore() {
		return ispScore;
	}
	public void setIspScore(Double ispScore) {
		this.ispScore = ispScore;
	}
	public Double getIpusageScore() {
		return ipusageScore;
	}
	public void setIpusageScore(Double ipusageScore) {
		this.ipusageScore = ipusageScore;
	}
	
	public Map<String, Object> allFields() {
		return allFields;
	}
	public void putFieldValue(String fieldName, Object value) {
		allFields.put(fieldName, value);
	}
	
}
