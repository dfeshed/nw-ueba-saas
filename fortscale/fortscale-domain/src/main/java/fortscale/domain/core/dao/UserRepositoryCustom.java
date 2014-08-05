package fortscale.domain.core.dao;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.Threshold;

public interface UserRepositoryCustom {
	public User findByApplicationUserName(ApplicationUserDetails applicationUserDetails);
	public List<User> findByApplicationUserName(String applicationName, List<String> usernames);
	public User findByApplicationUserName(String applicationName, String username);
	public Page<User> findByClassifierIdAndScoreBetweenAndTimeGteAsData(String classifierId, int lowestVal, int upperVal, Date time, Pageable pageable);
	public Page<User> findByClassifierIdAndFollowedAndScoreBetweenAndTimeGteAsData(String classifierId, int lowestVal, int upperVal, Date time, Pageable pageable);
	public Page<User> findByClassifierIdAndTimeGteAsData(String classifierId, Date time, Pageable pageable);
	public Page<User> findByClassifierIdAndFollowedAndTimeGteAsData(String classifierId, Date time, Pageable pageable);
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
	
	public User getLastActivityByUserName(String userName);
	
	public List<User> findByAdLastnameContaining(String lastNamePrefix);
	
	public User findByAdUserPrincipalName(String adUserPrincipalName);
	
	public List<User> findByAdUserPrincipalNameContaining(String adUserPrincipalNamePrefix);
	
	public User findByAdInfoDn(String adDn);
	
	public User findByAdInfoObjectGUID(String objectGUID);
	public User findByObjectGUID(String objectGUID);
	public HashMap<String, String> findAllUsernames();
	
	public User findLastActiveUser(LogEventsEnum eventId);
	
	public Set<String> findByUserInGroup(Collection<String> groups);
	public void updateUserTag(String tagField, String username, boolean value);
	public void updateCurrentUserScore(User user, String classifierId, double score, double trendScore, DateTime calculationTime);

	public long getNumberOfAccountsCreatedBefore(DateTime time);
	public long getNumberOfDisabledAccounts();
	public long getNumberOfDisabledAccountsBeforeTime(DateTime time);
	public long getNumberOfInactiveAccounts();
	
	public Set<String> findNameByTag(String tagFieldName, Boolean value);
	public boolean findIfUserExists(String username);
	public String getUserIdByNormalizedUsername(String username);
	/**
	 * Sync user tags according to the list of tags given (adds and removes neccesary tags)
	 */
	void syncTags(String username, List<String> tagsToAdd, List<String> tagsToRemove);
}
