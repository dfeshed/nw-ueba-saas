package fortscale.domain.events.dao;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.events.VpnSession;

public interface VpnSessionRepository extends MongoRepository<VpnSession,String>{
	public List<VpnSession> findByUsernameAndSourceIp(String username, String sourceIp, Pageable pageable);
	public List<VpnSession> findByUsernameAndCreatedAtEpochGreaterThan(String username, Long createdAtEpoch, Pageable pageable);
	public VpnSession findBySessionId(String sessionId);
	public List<VpnSession> findByUsernameAndCreatedAtEpochBetween(String normalizeUsername, Long createdAtEpochFrom, Long createdAtEpochTo, PageRequest pageRequest);
}
