package fortscale.services.fe;

import java.util.Map;

public interface IVpnEventScoreInfo {
	public String getUserId();
	public String getUsername();
	public Boolean isUserFollowed();
	public Map<String, Object> createMap();
}
