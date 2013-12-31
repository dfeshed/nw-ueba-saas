package fortscale.domain.ad.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.ad.AdUser;

public interface AdUserRepository extends PagingAndSortingRepository<AdUser, String>, AdUserRepositoryCustom{
	public AdUser findByEmailAddress(String emailAddress);
	public List<AdUser> findByTimestampepoch(Long timestampepoch);
	public AdUser findByTimestampepochAndObjectGUID(Long timestampepoch, String objectGUID);
	public List<AdUser> findByTimestampepoch(Long timestampepoch, Pageable pageable);
	public AdUser findByDistinguishedName(String distinguishedName);
	public List<AdUser> findByDistinguishedNameIgnoreCaseContaining(String distinguishedName);
	public List<AdUser> findByLastModifiedExists(boolean exists);
}
