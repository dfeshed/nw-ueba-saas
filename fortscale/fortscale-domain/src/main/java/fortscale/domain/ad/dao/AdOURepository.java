package fortscale.domain.ad.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.ad.AdOU;

public interface AdOURepository extends MongoRepository<AdOU, String>{
	public List<AdOU> findByLastModifiedExists(boolean exists);
}
