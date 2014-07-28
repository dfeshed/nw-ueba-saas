package fortscale.domain.core.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObjectBuilder;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.core.UserAdInfo;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.Threshold;

public class UserRepositoryImpl implements UserRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

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
		Query query = new Query(where(appUserNameField).regex(String.format("^%s$", username),"i"));
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

		return getPage(query, pageable, User.class);
	}

	@Override
	public Page<User> findByClassifierIdAndFollowedAndScoreBetweenAndTimeGteAsData(String classifierId, int lowestVal, int upperVal, Date time, Pageable pageable) {
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		String classifierCurScoreField = User.getClassifierScoreCurrentScoreField(classifierId);
		Query query = new Query(where(User.followedField).is(true).and(classifierCurScoreField).gte(lowestVal).lte(upperVal).and(classifierScoreCurrentTimestampField).gte(time));
		query.fields().exclude(User.adInfoField);
		
		return getPage(query, pageable, User.class);
	}
	
	private <T> Page<T> getPage(Query query, Pageable pageable, Class<T> entityClass){
		query.with(pageable);
		List<T> content = mongoTemplate.find(query, entityClass);
		long total = mongoTemplate.count(query, entityClass);

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<User> findByClassifierIdAndTimeGteAsData(String classifierId, Date time, Pageable pageable) {
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		Query query = new Query(where(classifierScoreCurrentTimestampField).gte(time));

		return getPage(query, pageable, User.class);
	}

	@Override
	public Page<User> findByClassifierIdAndFollowedAndTimeGteAsData(String classifierId, Date time, Pageable pageable) {
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		Query query = new Query(where(User.followedField).is(true).and(classifierScoreCurrentTimestampField).gte(time));

		return getPage(query, pageable, User.class);
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
	public List<User> findAllExcludeAdInfo() {
		Query query = new Query();
		query.fields().exclude(User.adInfoField);
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public HashMap<String, String> findAllUsernames() {
		Query query = new Query();
		query.fields().include(User.usernameField);
		HashMap<String, String> ret = new HashMap<>();
		for(UsernameWrapper username: mongoTemplate.find(query, UsernameWrapper.class, User.collectionName)){
			ret.put(username.getUsername(), username.getId());
		}

		return ret;
	}

	@Override
	public Set<String> findByUserInGroup(Collection<String> groups) {
		Query query = new Query(where(User.getAdInfoField(String.format("%s.%s",UserAdInfo.groupsField,UserAdInfo.adDnField))).in(groups));
		query.fields().include(User.usernameField);
		HashSet<String> userNames = new HashSet<String>();
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
	public User getLastActivityByUserName(String userName) {
		Criteria criteria = Criteria.where(User.usernameField).is(userName);
		Query query = new Query(criteria);
		query.fields().include(User.lastActivityField);
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
	public Set<String> findNameByTag(String tagFieldName, Boolean value) {
		Query query = new Query();
		Criteria criteria = where(tagFieldName).is(value);
		query.fields().include(User.usernameField);
		query.addCriteria(criteria);
		Set<String> res = new HashSet<String>();
		for(UsernameWrapper usernameWrapper : mongoTemplate.find(query, UsernameWrapper.class, User.collectionName)){
			res.add(usernameWrapper.getUsername());
		}
		return res;
	}
	
	public boolean findIfUserExists(String username){
		Query query = new Query(where(User.usernameField).is(username));
		query.fields().include(User.ID_FIELD);
		return !(mongoTemplate.find(query, UserIdWrapper.class, User.collectionName).isEmpty());
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
	
	class UsernameWrapper {
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
	
	class UserIdWrapper{
		private String id;
		
		public String getId(){
			return id;
		}
		public void setId(String id){
			this.id = id;
		}
	}
}
