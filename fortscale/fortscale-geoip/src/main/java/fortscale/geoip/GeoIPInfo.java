package fortscale.geoip;

public class GeoIPInfo {
	public static String RESERVED_RANGE = "Reserved Range";

	private String countryName = "";
	private String regionName = "";
	private String cityName = "";
	private String countryISOCode = "";
	private String ip = "";
	private String ISP = "";
	private String UsageType = "";

	public GeoIPInfo() {
	}

	public GeoIPInfo(String IPAddress) {
		setIp(IPAddress);
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
		ISP = iSP;
	}

	public String getUsageType() {
		return UsageType;
	}

	public void setUsageType(String usageType) {
		UsageType = usageType;
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

}
