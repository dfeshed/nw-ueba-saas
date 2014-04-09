package fortscale.domain.events.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.events.VpnSession;

public interface VpnSessionRepository extends MongoRepository<VpnSession,String>{
	public VpnSession findByNormalizeUsernameAndSourceIp(String normalizeUsername, String sourceIp);
}
