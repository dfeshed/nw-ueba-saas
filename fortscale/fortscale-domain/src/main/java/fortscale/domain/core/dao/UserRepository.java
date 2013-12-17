package fortscale.domain.core.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;





public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom{
	public User findByUsername(String username);
	public List<User> findByUsernameContaining(String username);
	public List<User> findByUsernameRegex(String usernameRegex);
	
	public List<User> findBySearchFieldContaining(String prefix);
	public List<User> findByFollowed(Boolean followed);
}
