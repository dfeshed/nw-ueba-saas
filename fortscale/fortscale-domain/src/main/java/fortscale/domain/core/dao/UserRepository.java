package fortscale.domain.core.dao;

import fortscale.domain.core.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;





public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom{
	public User findByUsername(String username);
	public User findByNoDomainUsername(String noDomainUsername);
	public List<User> findByUsernameContaining(String username);
	public List<User> findByUsernameRegex(String usernameRegex);
	public User findByAdDn(String adDn);
	public User findByAdObjectGUID(String adObjectGUID);
	public List<User> findBySearchFieldContaining(String prefix, Pageable pageable);
	public List<User> findByFollowed(Boolean followed);

}
