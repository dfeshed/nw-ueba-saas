package fortscale.domain.core.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;





public interface UserRepository extends PagingAndSortingRepository<User, Long>{
	/**
	 * Returns the {@link Customer} with the given {@link EmailAddress}.
	 * 
	 * @param string
	 * @return
	 */
	public User findByEmailAddress(EmailAddress emailAddress);
	
	public User findByAdDn(String adDn);
}
