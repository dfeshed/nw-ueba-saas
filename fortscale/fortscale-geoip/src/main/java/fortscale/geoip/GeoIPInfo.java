package fortscale.geoip;

import org.springframework.util.StringUtils;

public class GeoIPInfo {
	public static String RESERVED_RANGE = "Reserved Range";

	private String countryName = "";
	private String regionName = "";
	private String cityName = "";
	private String countryISOCode = "";
	private String ip = "";
	private String ISP = "";
	private IpUsageTypeEnum UsageType = null;
	private Double latitude;
	private Double longitude;

	public GeoIPInfo() {
	}

	public GeoIPInfo(String IPAddress) {
		setIp(IPAddress);
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = normalizeName(countryName);
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = normalizeName(regionName);
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = normalizeName(cityName);
	}

	public String getCountryISOCode() {
		return countryISOCode;
	}

	public void setCountryISOCode(String countryISOCode) {
		this.countryISOCode = countryISOCode;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}	

	public String getISP() {
		return ISP;
	}

	public void setISP(String iSP) {
		ISP = normalizeName(iSP);
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

	@Override
	public int hashCode() {
		return ip.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeoIPInfo other = (GeoIPInfo) obj;

		return ip.equals(other.ip);
	}
	
	private String normalizeName(String name){
		StringBuilder builder = new StringBuilder();
		for(String subname: name.toLowerCase().split(" ")){
			if(subname.isEmpty()){
				continue;
			}
			builder.append(StringUtils.capitalize(subname));
			builder.append(" ");
		}
		
		return builder.substring(0, builder.length() -1).toString();
	}

}
