package fortscale.services.event;

import fortscale.domain.events.VpnSession;

import java.util.List;




public interface VpnService {
	void createOpenVpnSession(VpnSession vpnSessionUpdate);
	VpnSession updateCloseVpnSession(VpnSession vpnSessionUpdate);
	void saveVpnSession(VpnSession vpnSession);
	List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession);
	List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession, int vpnGeoHoppingCloseSessionThresholdInHours, int vpnGeoHoppingOpenSessionThresholdInHours);
	VpnSession findOpenVpnSession(VpnSession closeVpnSession);
	VpnSession findBySessionId(String sessionId);
	List<VpnSession> findByUsernameAndCreatedAtEpochBetween(String normalizeUsername, Long createdAtEpochFrom, Long createdAtEpochTo);
	List<VpnSession> findByNormalizedUserNameAndCreatedAtEpochBetweenAndDurationExists(String normalizeUsername, Long createdAtEpochFrom, Long createdAtEpochTo);
}
