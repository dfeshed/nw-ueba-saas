package fortscale.domain.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import fortscale.domain.core.AbstractDocument;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;



@Document(collection=VpnSession.collectionName)
@CompoundIndexes({
	@CompoundIndex(name="usernameSourcIpIdx", def = "{'username': 1, 'sourceIp': 1}"),
	@CompoundIndex(name="usernameCreatedAtEpochIdx", def = "{'username': 1, 'createdAtEpoch': -1}"),
	@CompoundIndex(name="normalizedUsernameCreatedAtEpochIdx", def = "{'normalizedUsername': 1, 'createdAtEpoch': -1}"),
})
public class VpnSession extends AbstractDocument{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1247127683048664797L;

	public static final String collectionName =  "VpnSession";
	
	public static final String createdAtEpochFieldName = "createdAtEpoch";
	public static final String modifiedAtFieldName = "modifiedAt";
	
	
	
	@Indexed
	private String username;
	
	@Indexed
	private String sourceIp;
	
	@Indexed
	private String sessionId;
	@JsonIgnore
	private DateTime createdAt;
	@Field(createdAtEpochFieldName)
	private Long createdAtEpoch;
	@JsonIgnore
	private DateTime closedAt;
	
	private Long closedAtEpoch;

	@JsonIgnore
	private DateTime modifiedAt;
	
	private String localIp;

	@Indexed
	private String normalizedUserName;
	
	private String hostname;	
	
	private String country;
	
	private String countryIsoCode;
	
	private String region;
	
	private String city;
	
	private String isp;
	
	private String ispUsage;
	
	private Long totalBytes;
	
	private Long readBytes;
	
	private Long writeBytes;
	
	private Integer duration;
	
	private Integer dataBucket;
	
	private Double longtitude;
	
	private Double latitude;
	
	private Boolean geoHopping = false;

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Long getCreatedAtEpoch() {
		return createdAtEpoch;
	}

	public void setCreatedAtEpoch(Long createdAtEpoch) {
		this.createdAtEpoch = createdAtEpoch;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public DateTime getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(DateTime closedAt) {
		this.closedAt = closedAt;
	}

	public Long getClosedAtEpoch() {
		return closedAtEpoch;
	}

	public void setClosedAtEpoch(Long closedAtEpoch) {
		this.closedAtEpoch = closedAtEpoch;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public DateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(DateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getLocalIp() {
		return localIp;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryIsoCode() {
		return countryIsoCode;
	}

	public void setCountryIsoCode(String countryIsoCode) {
		this.countryIsoCode = countryIsoCode;
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

	public String getIspUsage() {
		return ispUsage;
	}

	public void setIspUsage(String ispUsage) {
		this.ispUsage = ispUsage;
	}

	public Long getTotalBytes() {
		return totalBytes;
	}

	public void setTotalBytes(Long totalBytes) {
		this.totalBytes = totalBytes;
	}

	public Long getReadBytes() {
		return readBytes;
	}

	public void setReadBytes(Long readBytes) {
		this.readBytes = readBytes;
	}

	public Long getWriteBytes() {
		return writeBytes;
	}

	public void setWriteBytes(Long writeBytes) {
		this.writeBytes = writeBytes;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getDataBucket() {
		return dataBucket;
	}

	public void setDataBucket(Integer dataBucket) {
		this.dataBucket = dataBucket;
	}

	public Double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(Double longtitude) {
		this.longtitude = longtitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Boolean getGeoHopping() {
		return geoHopping;
	}

	public void setGeoHopping(Boolean geoHopping) {
		this.geoHopping = geoHopping;
	}


	public String getNormalizedUserName() {
		return normalizedUserName;
	}

	public void setNormalizedUserName(String normalizedUserName) {
		this.normalizedUserName = normalizedUserName;
	}
}
