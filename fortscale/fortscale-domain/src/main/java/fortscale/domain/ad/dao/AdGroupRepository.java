package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdGroupRepository  extends MongoRepository<AdGroup,String>, AdGroupRepositoryCustom{
	List<AdGroup> findByLastModifiedExists(boolean exists);
	String findByName(String adName);
	List<AdGroup> findByNameStartingWithIgnoreCase(String startsWith);
}
