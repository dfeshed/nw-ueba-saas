package fortscale.services.event;

import java.util.List;

import fortscale.domain.events.VpnSession;
import org.springframework.data.domain.PageRequest;


public interface VpnService {
	void createOpenVpnSession(VpnSession vpnSessionUpdate);
	void updateCloseVpnSession(VpnSession vpnSessionUpdate);
	void saveVpnSession(VpnSession vpnSession);
	List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession);
	List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession, int vpnGeoHoppingCloseSessionThresholdInHours, int vpnGeoHoppingOpenSessionThresholdInHours);
	VpnSession findOpenVpnSession(VpnSession closeVpnSession);
	VpnSession findBySessionId(String sessionId);
	List<VpnSession> findByUsernameAndCreatedAtEpochBetween(String normalizeUsername, Long createdAtEpochFrom, Long createdAtEpochTo);
	List<VpnSession> findByNormalizedUserNameAndCreatedAtEpochBetweenAndDurationExists(String normalizeUsername, Long createdAtEpochFrom, Long createdAtEpochTo);
}
