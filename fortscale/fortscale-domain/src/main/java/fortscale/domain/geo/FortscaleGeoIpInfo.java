package fortscale.domain.geo;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import fortscale.domain.core.AbstractDocument;
import fortscale.geoip.IGeoIPInfo;
import fortscale.geoip.IpUsageTypeEnum;


@Document(collection=FortscaleGeoIpInfo.collectionName)
public class FortscaleGeoIpInfo extends AbstractDocument implements IGeoIPInfo{

	private static final long serialVersionUID = 3096533758690457663L;
	public static final String collectionName =  "geoIp";
	
	
	
	@Indexed(unique=true)
	private String ip;
	private String countryName;
	private String regionName;
	private String cityName;
	private String countryISOCode;
	private String ISP;
	private IpUsageTypeEnum UsageType;
	private Double latitude;
	private Double longitude;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCountryISOCode() {
		return countryISOCode;
	}
	public void setCountryISOCode(String countryISOCode) {
		this.countryISOCode = countryISOCode;
	}
	public String getISP() {
		return ISP;
	}
	public void setISP(String iSP) {
		ISP = iSP;
	}
	public IpUsageTypeEnum getUsageType() {
		return UsageType;
	}
	public void setUsageType(IpUsageTypeEnum usageType) {
		UsageType = usageType;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}
