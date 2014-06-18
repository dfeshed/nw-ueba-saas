package fortscale.domain.fe;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class VpnScore {	
	
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
	private String countrycode;
	private String hostname;
	private Long date_time_unix;
	
	private Double hostnameScore;
	private Double date_timeScore;
	private Double countryScore;
	
	private Double eventScore;
	
	
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
	
	public Map<String, Object> allFields() {
		return allFields;
	}
	public void putFieldValue(String fieldName, Object value) {
		allFields.put(fieldName, value);
	}
	public String getCountrycode() {
		return countrycode;
	}
	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public Long getDate_time_unix() {
		return date_time_unix;
	}
	public void setDate_time_unix(Long date_time_unix) {
		this.date_time_unix = date_time_unix;
	}
	public Double getHostnameScore() {
		return hostnameScore;
	}
	public void setHostnameScore(Double hostnameScore) {
		this.hostnameScore = hostnameScore;
	}
	
}
