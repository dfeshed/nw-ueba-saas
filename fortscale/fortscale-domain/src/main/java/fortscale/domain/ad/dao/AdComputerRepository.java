package fortscale.domain.ad.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.ad.AdComputer;

public interface AdComputerRepository extends MongoRepository<AdComputer, String>{
	public List<AdComputer> findByLastModifiedExists(boolean exists);
	public long countByTimestampepoch(Long timestampepoch);
}
