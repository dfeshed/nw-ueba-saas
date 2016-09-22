package fortscale.domain.core.dao;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.rest.UserRestFilter;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.*;

public interface UserRepositoryCustom {
	User findByApplicationUserName(ApplicationUserDetails applicationUserDetails);

	List<User> findByApplicationUserName(String applicationName, List<String> usernames);

	User findByApplicationUserName(String applicationName, String username);
	int countAllUsers(List<Criteria> criteriaList);

	User findByLogUsername(String logname, String username);

	void updateFollowed(User user, boolean followed);

	List<User> findByDNs(Collection<String> dns);

	List<User> findByGUIDs(Collection<String> guids);

	List<User> findByIds(Collection<String> ids);

	List<User> findByUsernames(Collection<String> usernames);
	Set<String> findByUsernameRegex(String usernameRegex);

	List<User> findUsersBysAMAccountName(String usernames);

	List<User> findByUsernamesExcludeAdInfo(Collection<String> usernames);

	List<User> findAllExcludeAdInfo(Pageable pageable);

	List<User> findAllUsers(Pageable pageable);

	List<User> findAllUsers(List<Criteria> criteriaList, Pageable pageable, List<String> fieldsRequired);

	Map<String, Long> groupByTags();

	User findByAdEmailAddress(EmailAddress emailAddress);

	User getLastActivityAndLogUserNameByUserName(String userName);

	List<User> getUsersActiveSinceIncludingUsernameAndLogLastActivity(DateTime date);

	@Deprecated
	User getLastActivityByUserName(String eventId, String username);

	List<User> findByAdLastnameContaining(String lastNamePrefix);

	User findByAdUserPrincipalName(String adUserPrincipalName);

	List<User> findByAdUserPrincipalNameContaining(String adUserPrincipalNamePrefix);

	User findByAdInfoDn(String adDn);

	User findByAdInfoObjectGUID(String objectGUID);

	User findByObjectGUID(String objectGUID);

	User findLastActiveUser(String logEventName);

	Set<String> findByUserInGroup(Collection<String> groups, Pageable pageable);

	Set<String> findByUserInOU(Collection<String> ouList, Pageable pageable);

	void updateUserTag(String tagField, String username, boolean value);

	long getNumberOfAccountsCreatedBefore(DateTime time);

	long getNumberOfDisabledAccounts();

	long getNumberOfDisabledAccountsBeforeTime(DateTime time);

	long getNumberOfInactiveAccounts();

	long getNumberOfTrackedAccounts();

	Set<String> findNameByTag(String tag, Pageable pageable);
	boolean findIfUserExists(String username);

	String getUserIdByNormalizedUsername(String username);

	HashSet<String> getUsersGUID();

	/**
	 * Sync user tags according to the list of tags given (adds and removes neccesary tags)
	 */
	Set<String> syncTags(String username, List<String> tagsToAdd, List<String> tagsToRemove);
	Set<String> getUserTags(String normalizedUsername);

	List<Map<String, String>> getUsersByPrefix(String prefix, Pageable pageable);

	List<Map<String, String>> getUsersByIds(String ids, Pageable pageable);

	/**
	 * count how many
	 *
	 * @param fieldName   -the field name to count
	 * @param fieldValues - the values to filter according.
	 * @return for each value in fieldValues, how many time it apears in the column fieldName
	 */
	Map<String, Integer> groupCount(String fieldName, Set<String> fieldValues);

	/**
	 * This method return username based on other AD field information (i.e - username--->DN_value)
	 *
	 * @param aDFieldName    -  the AD field to be based on the search
	 * @param aDFieldValue   - the AD given field value
	 * @param partOrFullFlag -  will sign if to do part ore full equalisation ( true - full , false -part (contain) )
	 * @return
	 */
	String findByfield(String aDFieldName, String aDFieldValue, boolean partOrFullFlag);

	List<Criteria> getUsersCriteriaByFilters(UserRestFilter userRestFilter);

	Criteria getUserCriteriaByUserIds(Set<String> userIds);
}

