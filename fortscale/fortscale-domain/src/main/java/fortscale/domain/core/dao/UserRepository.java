package fortscale.domain.core.dao;

import fortscale.domain.core.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom{
	User findByUsername(String username);
	User findByNoDomainUsername(String noDomainUsername);
	List<User> findByUsernameContaining(String username);
	User findByAdDn(String adDn);
	User findByAdObjectGUID(String adObjectGUID);
	List<User> findBySearchFieldContaining(String prefix, Pageable pageable);
	List<User> findByFollowed(Boolean followed);
}
