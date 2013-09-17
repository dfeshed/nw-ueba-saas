package fortscale.services.fe;

import java.util.Date;

public interface ILoginEventScoreInfo {
	public String getUserId();
	public String getUsername();
	public String getSourceIp();
	public String getSourceHostname();
	public Date getEventTime();
	public String getDestinationIp();
	public String getDestinationHostname();
	public double getEventScore();
	public double getUserScore();
}
