package fortscale.services.event;

import fortscale.domain.events.VpnSession;




public interface VpnService {
	public void createOrUpdateOpenVpnSession(VpnSession vpnSessionUpdate);
	public void updateCloseVpnSession(VpnSession vpnSessionUpdate);
}
