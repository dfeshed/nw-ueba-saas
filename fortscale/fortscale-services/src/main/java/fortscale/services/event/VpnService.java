package fortscale.services.event;

import java.util.List;

import fortscale.domain.events.VpnSession;




public interface VpnService {
	void createOrUpdateOpenVpnSession(VpnSession vpnSessionUpdate);
	void updateCloseVpnSession(VpnSession vpnSessionUpdate);
	void saveVpnSession(VpnSession vpnSession);
	List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession);
}
