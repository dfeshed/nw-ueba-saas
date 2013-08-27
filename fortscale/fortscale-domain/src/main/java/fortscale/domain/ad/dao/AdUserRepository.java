package fortscale.domain.ad.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.ad.AdUser;

public interface AdUserRepository extends PagingAndSortingRepository<AdUser, ObjectId>, AdUserRepositoryCustom{
	public AdUser findByEmailAddress(String emailAddress);
	public List<AdUser> findByTimestamp(String timestamp);
	public List<AdUser> findByDistinguishedNameIgnoreCaseContaining(String distinguishedName);
}
