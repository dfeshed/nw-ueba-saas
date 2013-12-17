package fortscale.domain.core.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.fe.dao.Threshold;

public interface UserRepositoryCustom {
	public User findByApplicationUserName(ApplicationUserDetails applicationUserDetails);
	public List<User> findByApplicationUserName(String applicationName, List<String> usernames);
	public User findByApplicationUserName(String applicationName, String username);
	public List<User> findByClassifierIdAndScoreBetween(String classifierId, int lowestVal, int upperVal, Pageable pageable);
	public List<User> findByClassifierIdAndFollowedAndScoreBetween(String classifierId, int lowestVal, int upperVal, Pageable pageable);
	public int countNumOfUsersAboveThreshold(String classifierId, Threshold threshold);
	public int countNumOfUsers(String classifierId);
	public User findByLogUsername(String logname, String username);
	public void updateFollowed(User user, boolean followed);
	public List<User> findByDNs(Collection<String> dns);
	public List<User> findByIds(Collection<String> ids);
	
	/**
	 * Returns the {@link Customer} with the given {@link EmailAddress}.
	 * 
	 * @param string
	 * @return
	 */
	public User findByAdEmailAddress(EmailAddress emailAddress);
	
	public List<User> findByAdLastnameContaining(String lastNamePrefix);
	
	public User findByAdUserPrincipalName(String adUserPrincipalName);
	
	public List<User> findByAdUserPrincipalNameContaining(String adUserPrincipalNamePrefix);
	
	public User findByAdDn(String adDn);
}
