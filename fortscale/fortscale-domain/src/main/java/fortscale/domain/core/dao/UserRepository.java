package fortscale.domain.core.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;





public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom{
	/**
	 * Returns the {@link Customer} with the given {@link EmailAddress}.
	 * 
	 * @param string
	 * @return
	 */
	public User findByEmailAddress(EmailAddress emailAddress);
	
	public List<User> findByLastnameContaining(String lastNamePrefix);
	
	public User findByAdUserPrincipalName(String adUserPrincipalName);
	
	public User findByUsername(String username);
	public List<User> findByUsernameContaining(String username);
	
	public List<User> findByAdUserPrincipalNameContaining(String adUserPrincipalNamePrefix);
	
	public User findByAdDn(String adDn);
	
	public List<User> findBySearchFieldContaining(String prefix);
}
