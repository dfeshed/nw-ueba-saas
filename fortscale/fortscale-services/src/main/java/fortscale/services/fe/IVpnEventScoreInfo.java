package fortscale.services.fe;

import java.util.Date;
import java.util.Map;

public interface IVpnEventScoreInfo {
	public String getUserId();
	public String getUsername();
	public String getSourceIp();
	public Date getEventTime();
	public String getInternalIP();
	public double getEventScore();
	public String getStatus();
	public String getCountry();
	public String getRegion();
	public String getCity();
	public String getIsp();
	public String getIpusage();
	public double getEventTimeScore();
	public double getCountryScore();
	public double getRegionScore();
	public double getCityScore();
	public Boolean isUserFollowed();
	public Map<String, Object> createMap();
}
