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
	public List<User> findByClassifierIdAndScoreBetweenAndCurrentDay(String classifierId, int lowestVal, int upperVal, Pageable pageable);
	public List<User> findByClassifierIdAndFollowedAndScoreBetweenAndCurrentDay(String classifierId, int lowestVal, int upperVal, Pageable pageable);
	public List<User> findByClassifierIdAndCurrentDay(String classifierId, Pageable pageable);
	public List<User> findByClassifierIdAndFollowedAndCurrentDay(String classifierId, Pageable pageable);
	public int countNumOfUsersAboveThreshold(String classifierId, Threshold threshold);
	public int countNumOfUsers(String classifierId);
	public User findByLogUsername(String logname, String username);
	public void updateFollowed(User user, boolean followed);
	public List<User> findByDNs(Collection<String> dns);
	public List<User> findByGUIDs(Collection<String> guids);
	public List<User> findByIds(Collection<String> ids);
	public List<User> findByUsernames(Collection<String> usernames);
	public List<User> findAllExcludeAdInfo();

	public User findByAdEmailAddress(EmailAddress emailAddress);
	
	public List<User> findByAdLastnameContaining(String lastNamePrefix);
	
	public User findByAdUserPrincipalName(String adUserPrincipalName);
	
	public List<User> findByAdUserPrincipalNameContaining(String adUserPrincipalNamePrefix);
	
	public User findByAdInfoDn(String adDn);
	
	public User findByAdInfoObjectGUID(String objectGUID);
	User findByObjectGUID(String objectGUID);
}
