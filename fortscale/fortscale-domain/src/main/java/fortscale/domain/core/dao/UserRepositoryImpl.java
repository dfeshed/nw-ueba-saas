
package fortscale.domain.core.dao;

import com.mongodb.BasicDBObjectBuilder;
import fortscale.domain.ad.AdUser;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.core.UserAdInfo;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.Threshold;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

public class UserRepositoryImpl implements UserRepositoryCustom {

	private static Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private MongoDbRepositoryUtil mongoDbRepositoryUtil;

	@Override
	public User findLastActiveUser(LogEventsEnum eventId) {
		String logLastActiveField = User.getLogLastActivityField(eventId);
		Pageable pageable = new PageRequest(0, 1, Direction.DESC, logLastActiveField);
		Query query = new Query();
		query.with(pageable);
		return mongoTemplate.findOne(query, User.class);
	}

	@Override
	public User findByApplicationUserName(ApplicationUserDetails applicationUserDetails) {
		String appUserNameField = User.getAppUserNameField(applicationUserDetails.getApplicationName());
		Query query = new Query(where(appUserNameField).is(applicationUserDetails.getUserName()));
		return mongoTemplate.findOne(query, User.class);
	}

	@Override
	public User findByObjectGUID(String objectGUID) {
		Query query = new Query(where(User.getAdInfoField(UserAdInfo.objectGUIDField)).is(objectGUID));
		return mongoTemplate.findOne(query, User.class);
	}

	@Override
	public List<User> findByApplicationUserName(String applicationName, List<String> usernames){
		String appUserNameField = User.getAppUserNameField(applicationName);
		Criteria criterias[] = new Criteria[usernames.size()];
		for (int i = 0; i < usernames.size(); i++) {
			String username = usernames.get(i);
			criterias[i] = where(appUserNameField).regex(String.format("^%s$", username),"i");
		}
		Query query = new Query(new Criteria().orOperator(criterias));
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public User findByApplicationUserName(String applicationName, String username){
		String appUserNameField = User.getAppUserNameField(applicationName);
		Query query = new Query(where(appUserNameField).regex(String.format("^%s$", username), "i"));
		return mongoTemplate.findOne(query, User.class);
	}

	@Override
	public User findByLogUsername(String logname, String username) {
		String logUsernameField = User.getLogUserNameField(logname);
		Query query = new Query(where(logUsernameField).is(username));
		return mongoTemplate.findOne(query, User.class);
	}

	@Override
	public Page<User> findByClassifierIdAndScoreBetweenAndTimeGteAsData(String classifierId, int lowestVal, int upperVal, Date time, Pageable pageable) {
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		String classifierCurScoreField = User.getClassifierScoreCurrentScoreField(classifierId);
		Query query = new Query(where(classifierCurScoreField).gte(lowestVal).lte(upperVal).and(classifierScoreCurrentTimestampField).gte(time));
		query.fields().exclude(User.adInfoField);

		return mongoDbRepositoryUtil.getPage(query, pageable, User.class, true);
	}

	@Override
	public Page<User> findByClassifierIdAndFollowedAndScoreBetweenAndTimeGteAsData(String classifierId, int lowestVal, int upperVal, Date time, Pageable pageable) {
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		String classifierCurScoreField = User.getClassifierScoreCurrentScoreField(classifierId);
		Query query = new Query(where(User.followedField).is(true).and(classifierCurScoreField).gte(lowestVal).lte(upperVal).and(classifierScoreCurrentTimestampField).gte(time));
		query.fields().exclude(User.adInfoField);
		
		return mongoDbRepositoryUtil.getPage(query, pageable, User.class, true);
	}
	

	@Override
	public Page<User> findByClassifierIdAndTimeGteAsData(String classifierId, Date time, Pageable pageable) {
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		Query query = new Query(where(classifierScoreCurrentTimestampField).gte(time));

		return mongoDbRepositoryUtil.getPage(query, pageable, User.class, true);
	}

	@Override
	public Page<User> findByClassifierIdAndFollowedAndTimeGteAsData(String classifierId, Date time, Pageable pageable) {
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		Query query = new Query(where(User.followedField).is(true).and(classifierScoreCurrentTimestampField).gte(time));

		return mongoDbRepositoryUtil.getPage(query, pageable, User.class, true);
	}

	@Override
	public int countNumOfUsersAboveThreshold(String classifierId, Threshold threshold) {
		DateTime dateTime = new DateTime();
		dateTime = dateTime.minusDays(1);
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		String classifierCurScoreField = User.getClassifierScoreCurrentScoreField(classifierId);
		Query query = new Query(where(classifierCurScoreField).gte(threshold.getValue()).and(classifierScoreCurrentTimestampField).gte(dateTime.toDate()));
		return (int) mongoTemplate.count(query, User.class);
	}

	@Override
	public int countNumOfUsers(String classifierId) {
		DateTime dateTime = new DateTime();
		dateTime = dateTime.minusDays(1);
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		Query query = new Query(where(classifierScoreCurrentTimestampField).gte(dateTime.toDate()));
		return (int) mongoTemplate.count(query, User.class);
	}

	@Override
	public void updateFollowed(User user, boolean followed) {
		mongoTemplate.updateFirst(query(where(User.ID_FIELD).is(user.getId())), update(User.followedField, followed), User.class);
	}

	@Override
	public List<User> findByDNs(Collection<String> dns) {
		return findByUniqueField(User.getAdInfoField(UserAdInfo.adDnField), dns);
	}

	@Override
	public List<User> findByGUIDs(Collection<String> guids) {
		return findByUniqueField(User.getAdInfoField(UserAdInfo.objectGUIDField),guids);
	}

	private List<User> findByUniqueField(String fieldName, Collection<?> vals) {
		// Criteria criterias[] = new Criteria[vals.size()];
		// int i = 0;
		// for(Object val: vals){
		// criterias[i] = where(fieldName).is(val);
		// i++;
		// }
		Query query = new Query(where(fieldName).in(vals));
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public List<User> findByIds(Collection<String> ids) {
		return findByUniqueField(User.ID_FIELD, ids);
	}

	@Override
	public List<User> findByUsernames(Collection<String> usernames) {
		return findByUniqueField(User.usernameField, usernames);
	}

	@Override
	public List<User> findUsersBysAMAccountName(String username) {
		PageRequest pageRequest = new PageRequest(0, 1000);
		return findByFieldCaseInsensitive(User.getAdInfoField(UserAdInfo.sAMAccountNameField), username, pageRequest);
	}

	@Override
	public List<User> findByUsernamesExcludeAdInfo(Collection<String> usernames) {
		Query query = new Query(where(User.usernameField).in(usernames));
		query.fields().exclude(User.adInfoField);
		return mongoTemplate.find(query, User.class);
	}
	
	@Override
	public User findByAdEmailAddress(EmailAddress emailAddress) {
		return findOneByField(User.getAdInfoField(UserAdInfo.emailAddressField), emailAddress);
	}

	@Override
	public List<User> findByAdLastnameContaining(String lastNamePrefix) {
		Query query = new Query(where(User.getAdInfoField(UserAdInfo.lastnameField)).regex(String.format("^%s$", lastNamePrefix),"i"));
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public User findByAdUserPrincipalName(String adUserPrincipalName) {
		return findOneByField(User.getAdInfoField(UserAdInfo.userPrincipalNameField), adUserPrincipalName);
	}

	@Override
	public List<User> findByAdUserPrincipalNameContaining(String adUserPrincipalNamePrefix) {
		Query query = new Query(where(User.getAdInfoField(UserAdInfo.userPrincipalNameField)).regex(String.format("^%s$", adUserPrincipalNamePrefix),"i"));
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public User findByAdInfoDn(String adDn) {
		return findOneByField(User.getAdInfoField(UserAdInfo.adDnField), adDn);
	}

	@Override
	public User findByAdInfoObjectGUID(String objectGUID) {
		return findOneByField(User.getAdInfoField(UserAdInfo.objectGUIDField), objectGUID);
	}

	private List<User> findByField(String field, Object val, Pageable pageable) {
		List<String> fields = new ArrayList<>();
		fields.add(field);
		List<Object> vals = new ArrayList<>();
		vals.add(val);
		return findByFields(fields, vals, pageable);
	}

	private List<User> findByFieldCaseInsensitive(String field, Object val, Pageable pageable) {
		List<User> result;
		try {
			Criteria criteria = where(field).regex(Pattern.compile("^" + val.toString() + "$", Pattern.CASE_INSENSITIVE));
			Query query = new Query(criteria);
			if (pageable != null) {
				query.with(pageable);
			}
			result = mongoTemplate.find(query, User.class);
		} catch (Exception ex) {
			//exception can happen in the case where users have special characters such as '*' in their samaccountname
			logger.warn("Failed to search for samaccountname value - " + val.toString() + ", due to special character");
			result = new ArrayList<User>();
		}
		return result;
	}

	private User findOneByField(String field, Object val) {
		PageRequest pageRequest = new PageRequest(0, 1);
		List<User> users = findByField(field, val, pageRequest);
		if (users.size() > 0) {
			return users.get(0);
		} else {
			return null;
		}
	}
	
	private List<User> findByFields(List<String> fields, List<?> vals, Pageable pageable){
		Criteria criteria = where(fields.get(0)).is(vals.get(0));
		for (int i = 1; i < fields.size(); i++) {
			criteria.and(fields.get(i)).is(vals.get(i));
		}
		Query query = new Query(criteria);
		if (pageable != null) {
			query.with(pageable);
		}
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public List<User> findAllExcludeAdInfo(Pageable pageable) {
		Query query = new Query().with(pageable);
		query.fields().exclude(User.adInfoField);
		return mongoTemplate.find(query, User.class);
	}


	@Override
	public List<User> findAllUsers(Pageable pageable) {
		Query query = new Query().with(pageable);
		return mongoTemplate.find(query, User.class);
	}


	@Override
	public Set<String> findByUserInGroup(Collection<String> groups, Pageable pageable) {
		Query query = new Query().with(pageable);
		String adInfoFieldName = String.format("%s.%s", UserAdInfo.groupsField, UserAdInfo.adDnField);
		query.addCriteria(where(User.getAdInfoField(adInfoFieldName)).in(groups));
		query.fields().include(User.usernameField);
		return getUsernameFromWrapper(query);
	}

	@Override
	public Set<String> findByUserInOU(Collection<String> ouList, Pageable pageable) {
		// Get users according to OU (users that their DN ends with the requested OU)
		StringBuffer ouRegexp = new StringBuffer();
		for (String ou : ouList)
			ouRegexp.append("|,").append(ou).append("$");
		Query query = new Query().with(pageable);
		String adInfoField = User.getAdInfoField(UserAdInfo.adDnField);
		query.addCriteria(where(adInfoField).regex(ouRegexp.substring(1), "i"));

		// Take only the username field from the document
		query.fields().include(User.usernameField);

		// Take only the users' names
		return getUsernameFromWrapper(query);
	}

	private Set<String> getUsernameFromWrapper(Query query) {

		HashSet<String> userNames = new HashSet<>();
		for (UsernameWrapper userNameWrapper : mongoTemplate.find(query, UsernameWrapper.class, User.collectionName)){
			userNames.add(userNameWrapper.getUsername());
		}
		return userNames;
	}

	@Override
	public void updateUserTag(String tagField, String username, boolean value) {
		mongoTemplate.updateFirst(query(where(User.usernameField).is(username)), update(tagField, value), User.class);
	}
	
	@Override
	public void updateCurrentUserScore(User user, String classifierId, double score, double trendScore, DateTime calculationTime){
		Update update = new Update();
		update.set(User.getClassifierScoreCurrentScoreField(classifierId), score);
		update.set(User.getClassifierScoreCurrentTrendScoreField(classifierId), trendScore);
		update.set(User.getClassifierScoreCurrentTimestampField(classifierId), calculationTime.toDate());
		update.set(User.getClassifierScoreCurrentTimestampEpochField(classifierId), calculationTime.getMillis());
		
		mongoTemplate.updateFirst(query(where(User.ID_FIELD).is(user.getId())), update, User.class);
	}



	@Override
	public User getLastActivityAndLogUserNameByUserName(String userName) {
		Criteria criteria = Criteria.where(User.usernameField).is(userName);
		Query query = new Query(criteria);
		query.fields().include(User.lastActivityField);
		query.fields().include(User.logLastActivityField);
		query.fields().include(User.logUsernameField);
		List<User> users = mongoTemplate.find(query, User.class);

		if (users.size() > 0) {
			return users.get(0);
		} else {
			return null;
		}
	}

	@Deprecated
	@Override
	public User getLastActivityByUserName(LogEventsEnum eventId, String userName) {
		Criteria criteria = Criteria.where(User.usernameField).is(userName);
		Query query = new Query(criteria);
		query.fields().include(User.lastActivityField);
		query.fields().include(User.getLogLastActivityField(eventId));
		List<User> users = mongoTemplate.find(query, User.class);

		if (users.size() > 0) {
			return users.get(0);
		} else {
			return null;
		}
	}

	@Override
	public long getNumberOfAccountsCreatedBefore(DateTime time){
		Criteria criteria = Criteria.where(User.whenCreatedField).lt(
				time);
		Query query = new Query(criteria);
		return mongoTemplate.count(query, User.class);
	}

	@Override
	public long getNumberOfDisabledAccounts() {
		Query query = new Query(Criteria.where(
				User.getAdInfoField(UserAdInfo.isAccountDisabledField))
				.is(true).and(User.getAdInfoField(UserAdInfo.disableAccountTimeField))
				.ne(null));
		return mongoTemplate.count(query, User.class);
	}

	@Override
	public long getNumberOfDisabledAccountsBeforeTime(DateTime time) {
		Criteria criteria = Criteria
				.where(User.getAdInfoField(UserAdInfo.isAccountDisabledField))
				.is(true)
				.and(User.getAdInfoField(UserAdInfo.disableAccountTimeField))
				.lt(time);
		Query query = new Query(criteria);
		return mongoTemplate.count(query, User.class);
	}

	@Override
	public long getNumberOfInactiveAccounts() {
		//temporary implementation
		Query query = new Query(Criteria.where(
				User.lastActivityField)
				.is(null));
		return mongoTemplate.count(query, User.class);
	}
	
	
	public void syncTags(String username, List<String> tagsToAdd, List<String> tagsToRemove) {
		// construct the criteria to filter according to user name
		Query usernameCriteria = new Query(Criteria.where(User.usernameField).is(username));

		// construct the update that adds and removes tags
		if (!tagsToAdd.isEmpty()) {
            EachAddToSetUpdate update = new EachAddToSetUpdate();
            update.addToSetEach(User.tagsField, tagsToAdd);

            // perform the update on mongodb
            mongoTemplate.updateFirst(usernameCriteria, update, User.class);
        }

		if (!tagsToRemove.isEmpty()) {
            EachAddToSetUpdate update = new EachAddToSetUpdate();
            update.pullAll(User.tagsField, tagsToRemove.toArray());

            // perform the update on mongodb
            mongoTemplate.updateFirst(usernameCriteria, update, User.class);
        }
	}

	@Override
	public Set<String> findNameByTag(String tagFieldName, Boolean value, Pageable pageable) {
		Query query = new Query().with(pageable);
		Criteria criteria = where(tagFieldName).is(value);
		query.fields().include(User.usernameField);
		query.addCriteria(criteria);
		Set<String> res = new HashSet<String>();
		for (UsernameWrapper usernameWrapper : mongoTemplate.find(query, UsernameWrapper.class, User.collectionName))
			res.add(usernameWrapper.getUsername());
		return res;
	}

	@Override
	public Set<String> findNameByTag(String tagFieldName, String value, Pageable pageable) {
		Query query = new Query().with(pageable);
		Criteria criteria = where(tagFieldName).is(value);
		query.fields().include(User.usernameField);
		query.addCriteria(criteria);
		Set<String> res = new HashSet<String>();
		for (UsernameWrapper usernameWrapper : mongoTemplate.find(query, UsernameWrapper.class, User.collectionName))
			res.add(usernameWrapper.getUsername());
		return res;
	}

	public Set<String> getUserTags(String normalizedUsername) {
		Query query = new Query(where(User.usernameField).is(normalizedUsername));
		query.fields().include(User.tagsField);
		User user = mongoTemplate.findOne(query, User.class);
		
		return (user==null)? new HashSet<String>() : user.getTags();
	}
	
	public boolean findIfUserExists(String username){
		Query query = new Query(where(User.usernameField).is(username));
		query.fields().include(User.ID_FIELD);
		return !(mongoTemplate.find(query, UserIdWrapper.class, User.collectionName).isEmpty());
	}
	
	public String getUserIdByNormalizedUsername(String username) {
		Query query = new Query(where(User.usernameField).is(username));
		query.fields().include(User.ID_FIELD);
 		UserIdWrapper wrapper = mongoTemplate.findOne(query, UserIdWrapper.class, User.collectionName);
 		return (wrapper==null)? null : wrapper.getId();
	}
	
	public HashSet<String> getUsersGUID(){
		Query query = new Query();
		query.fields().include(User.getAdInfoField(AdUser.objectGUIDField)).exclude(User.ID_FIELD);
		HashSet<String> res = new HashSet<String>();
		for(UserObjectGUIDWrapper userGUID : mongoTemplate.find(query, UserObjectGUIDWrapper.class, User.collectionName)){
			res.add(userGUID.getObjectGUID());
		}
		return res;
	}
	
	/**
	 * Since spring data mongodb does not support each on the addToSet update 
	 * operator we create a custom bson command that does that
	 */
	class EachAddToSetUpdate extends Update {
		
		public EachAddToSetUpdate addToSetEach(String key, List<String> values) {
			// create a custom addToSet operator
			this.addToSet(key, BasicDBObjectBuilder.start("$each", values).get());
			return this;
		}
	}
	
	static class UsernameWrapper {
		private String id;
		private String username;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}
	}
	
	static class UserIdWrapper{
		private String id;
		
		public String getId(){
			return id;
		}
		public void setId(String id){
			this.id = id;
		}
	}
	
	static public class UserObjectGUIDWrapper {
		private String objectGUID;

		public String getObjectGUID() {
			return objectGUID;
		}

		public void setObjectGUID(String objectGUID) {
			this.objectGUID = objectGUID;
		}


		public UserObjectGUIDWrapper(String objectGUID) {
			this.objectGUID = objectGUID;
		}
	}
	
}

