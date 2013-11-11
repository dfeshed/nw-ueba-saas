package fortscale.services.fe;

import java.util.Date;

public interface IVpnEventScoreInfo {
	public String getUserId();
	public String getUsername();
	public String getSourceIp();
	public String getSourceHostname();
	public Date getEventTime();
	public String getInternalIP();
	public String getInternalHostname();
	public double getEventScore();
	public double getUserScore();
	public String getStatus();
	public double getEventTimeScore();
	public double getUserNameScore();
	public double getSourceIpScore();
	public double getLocalIpScore();
	public double getStatusScore();
}
