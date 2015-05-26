package fortscale.domain.core.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.core.User;





public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom{
	public User findByUsername(String username);
	public List<User> findUsersByUsername(String username);
	public User findByNoDomainUsername(String noDomainUsername);
	public List<User> findByUsernameContaining(String username);
	public List<User> findByUsernameRegex(String usernameRegex);
	public User findByAdDn(String adDn);
	public User findByAdObjectGUID(String adObjectGUID);
	
	public List<User> findBySearchFieldContaining(String prefix, Pageable pageable);
	public List<User> findByFollowed(Boolean followed);

}
