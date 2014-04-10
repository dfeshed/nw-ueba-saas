package fortscale.domain.events.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.events.VpnSession;

public interface VpnSessionRepository extends MongoRepository<VpnSession,String>{
	public VpnSession findByNormalizeUsernameAndSourceIp(String normalizeUsername, String sourceIp);
	public List<VpnSession> findByNormalizeUsernameAndCreatedAtEpochGreaterThan(String normalizeUsername, Long createdAtEpoch, Pageable pageable);
}
