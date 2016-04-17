
package fortscale.domain.core.dao;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.fe.dao.Threshold;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.*;

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
	public int countAllUsers(List<Criteria> criteriaList);
	public User findByLogUsername(String logname, String username);
	public void updateFollowed(User user, boolean followed);
	public List<User> findByDNs(Collection<String> dns);
	public List<User> findByGUIDs(Collection<String> guids);
	public List<User> findByIds(Collection<String> ids);
	public List<User> findByUsernames(Collection<String> usernames);
	public List<User> findUsersBysAMAccountName(String usernames);
	public List<User> findByUsernamesExcludeAdInfo(Collection<String> usernames);
	public List<User> findAllExcludeAdInfo(Pageable pageable);
	public List<User> findAllUsers(Pageable pageable);
	public List<User> findAllUsers(List<Criteria> criteriaList, Pageable pageable);
	public Map<String, Long> groupByTags();

	public User findByAdEmailAddress(EmailAddress emailAddress);
	
	public User getLastActivityAndLogUserNameByUserName(String userName);
	@Deprecated
	public User getLastActivityByUserName(String eventId, String username);
	
	public List<User> findByAdLastnameContaining(String lastNamePrefix);
	
	public User findByAdUserPrincipalName(String adUserPrincipalName);
	
	public List<User> findByAdUserPrincipalNameContaining(String adUserPrincipalNamePrefix);
	
	public User findByAdInfoDn(String adDn);
	
	public User findByAdInfoObjectGUID(String objectGUID);
	public User findByObjectGUID(String objectGUID);
	public User findLastActiveUser(String logEventName);

	public Set<String> findByUserInGroup(Collection<String> groups, Pageable pageable);
	public Set<String> findByUserInOU(Collection<String> ouList, Pageable pageable);
	public void updateUserTag(String tagField, String username, boolean value);
	public void updateCurrentUserScore(User user, String classifierId, double score, double trendScore, DateTime calculationTime);

	public long getNumberOfAccountsCreatedBefore(DateTime time);
	public long getNumberOfDisabledAccounts();
	public long getNumberOfDisabledAccountsBeforeTime(DateTime time);
	public long getNumberOfInactiveAccounts();

	public Set<String> findNameByTag(String tagFieldName, Boolean value, Pageable pageable);
	public Set<String> findNameByTag(String tagFieldName, String value, Pageable pageable);
	public boolean findIfUserExists(String username);
	public String getUserIdByNormalizedUsername(String username);
	public HashSet<String> getUsersGUID();
	/**
	 * Sync user tags according to the list of tags given (adds and removes neccesary tags)
	 */
	Set<String> syncTags(String username, List<String> tagsToAdd, List<String> tagsToRemove);
	public Set<String> getUserTags(String normalizedUsername);
	public List<Map<String, String>> getUsersByPrefix(String prefix, Pageable pageable);
	public List<Map<String, String>> getUsersByIds(String ids, Pageable pageable);

	/**
	 * count how many
	 * @param fieldName  -the field name to count
	 * @param fieldValues  - the values to filter according.
	 *
	 * @return for each value in fieldValues, how many time it apears in the column fieldName
	 */
	public Map<String, Integer> groupCount(String fieldName, Set<String> fieldValues);

	/**
	 * This method return username based on other AD field information (i.e - username--->DN_value)
	 * @param aDFieldName -  the AD field to be based on the search
	 * @param aDFieldValue - the AD given field value
	 * @param partOrFullFlag -  will sign if to do part ore full equalisation ( true - full , false -part (contain) )
	 * @return
	 */
	public String findByfield(String aDFieldName,String aDFieldValue,boolean partOrFullFlag);
}

