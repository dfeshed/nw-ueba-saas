package fortscale.geoip;

public interface IGeoIPInfo {

	public String getCountryName();

	public String getRegionName();

	public String getCityName();

	public String getCountryISOCode();

	public String getIp();

	public String getISP();

	public IpUsageTypeEnum getUsageType();

	public Double getLatitude();

	public Double getLongitude();
}