package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdUserRepository extends MongoRepository<AdUser, String>, AdUserRepositoryCustom {
	AdUser findByMail(String mail);
	List<AdUser> findByTimestampepoch(Long timestampepoch);
	AdUser findByTimestampepochAndObjectGUID(Long timestampepoch, String objectGUID);
	List<AdUser> findByTimestampepoch(Long timestampepoch, Pageable pageable);
	AdUser findByDistinguishedName(String distinguishedName);
	List<AdUser> findByDistinguishedNameIgnoreCaseContaining(String distinguishedName);
	List<AdUser> findByLastModifiedExists(boolean exists);
}
