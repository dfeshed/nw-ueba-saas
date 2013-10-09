package fortscale.services.fe;

import java.util.Date;

public interface IVpnEventScoreInfo {
	public String getUserId();
	public String getUsername();
	public String getSourceIp();
	public String getSourceHostname();
	public Date getEventTime();
	public String getLocalIp();
	public String getLocalHostname();
	public double getEventScore();
	public double getUserScore();
}
