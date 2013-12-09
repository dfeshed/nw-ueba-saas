package fortscale.services.fe;

import java.util.Date;
import java.util.Map;

public interface ILoginEventScoreInfo {
	public String getUserId();
	public String getUsername();
	public String getSourceIp();
	public Date getEventTime();
	public String getDestinationHostname();
	public double getEventScore();
	public double getUserScore();
	public double getUserNameScore();
	public double getTargetIdScore();
	public double getSourceIpScore();
	public double getEventTimeScore();
	public Map<String, Object> createMap();
	public Boolean isUserFollowed();
}
