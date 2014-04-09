package fortscale.domain.events;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import fortscale.domain.core.AbstractDocument;



@Document(collection=VpnSession.collectionName)
@CompoundIndexes({
	@CompoundIndex(name="normalizeUsernameSourcIpIdx", def = "{'normalizeUsername': 1, 'sourceIp': 1}"),
	@CompoundIndex(name="normalizeUsernameCreatedAtEpochIdx", def = "{'normalizeUsername': 1, 'createdAtEpoch': -1}"),
})
public class VpnSession extends AbstractDocument{
	public static final String collectionName =  "VpnSession";
	
	
	
	
	
	@Indexed
	private String normalizeUsername;
	
	@Indexed
	private String sourceIp;
	
	private DateTime createdAt;
	
	private Long createdAtEpoch;
	
	@Indexed(unique = false, expireAfterSeconds=60*60*24*4)
	private DateTime closedAt;
	
	private Long closedAtEpoch;
	
	@Indexed(unique = false, expireAfterSeconds=60*60*24*30)
	private DateTime modifiedAt;
	
	private String localIp;
	
	private String hostname;	
	
	private String username;
	
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

	public String getNormalizeUsername() {
		return normalizeUsername;
	}

	public void setNormalizeUsername(String normalizeUsername) {
		this.normalizeUsername = normalizeUsername;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

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
}
