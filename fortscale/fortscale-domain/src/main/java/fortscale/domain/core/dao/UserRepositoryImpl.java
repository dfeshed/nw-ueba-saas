package fortscale.domain.core.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.core.UserAdInfo;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.Threshold;

public class UserRepositoryImpl implements UserRepositoryCustom{
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	
	
	@Override
	public User findLastActiveUser(LogEventsEnum eventId){
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
		for(int i = 0; i < usernames.size(); i++){
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
	public User findByLogUsername(String logname, String username){
		String logUsernameField = User.getLogUserNameField(logname);
		Query query = new Query(where(logUsernameField).is(username));
		return mongoTemplate.findOne(query, User.class);
	}

	@Override
	public Page<User> findByClassifierIdAndScoreBetweenAndTimeGteAsData(String classifierId, int lowestVal, int upperVal, Date time, Pageable pageable) {
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		String classifierCurScoreField = User.getClassifierScoreCurrentScoreField(classifierId);
		Query query = new Query(where(classifierCurScoreField).gte(lowestVal).lte(upperVal).and(classifierScoreCurrentTimestampField).gte(time));

		return getPage(query, pageable, User.class);
	}
	
	@Override
	public Page<User> findByClassifierIdAndFollowedAndScoreBetweenAndTimeGteAsData(String classifierId, int lowestVal, int upperVal, Date time, Pageable pageable) {
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		String classifierCurScoreField = User.getClassifierScoreCurrentScoreField(classifierId);
		Query query = new Query(where(User.followedField).is(true).and(classifierCurScoreField).gte(lowestVal).lte(upperVal).and(classifierScoreCurrentTimestampField).gte(time));
		
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
		return findByUniqueField(User.getAdInfoField(UserAdInfo.adDnField),dns);
	}
	
	@Override
	public List<User> findByGUIDs(Collection<String> guids) {
		return findByUniqueField(User.getAdInfoField(UserAdInfo.objectGUIDField),guids);
	}
	
	private List<User> findByUniqueField(String fieldName, Collection<?> vals) {
//		Criteria criterias[] = new Criteria[vals.size()];
//		int i = 0;
//		for(Object val: vals){
//			criterias[i] = where(fieldName).is(val);
//			i++;
//		}
		Query query = new Query(where(fieldName).in(vals));
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public List<User> findByIds(Collection<String> ids) {
		return findByUniqueField(User.ID_FIELD,ids);
	}
	
	@Override
	public List<User> findByUsernames(Collection<String> usernames){
		return findByUniqueField(User.usernameField,usernames);
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
		
	private List<User> findByField(String field, Object val, Pageable pageable){
		List<String> fields = new ArrayList<>();
		fields.add(field);
		List<Object> vals = new ArrayList<>();
		vals.add(val);
		return findByFields(fields, vals, pageable);
	}
	
	private User findOneByField(String field, Object val){
		PageRequest pageRequest = new PageRequest(0, 1);
		List<User> users = findByField(field, val, pageRequest);
		if(users.size() > 0){
			return users.get(0);
		} else{
			return null;
		}
	}
	
	private List<User> findByFields(List<String> fields, List<?> vals, Pageable pageable){
		Criteria criteria = where(fields.get(0)).is(vals.get(0));
		for(int i = 1; i < fields.size(); i++){
			criteria.and(fields.get(i)).is(vals.get(i));
		}
		Query query = new Query(criteria);
		if(pageable != null){
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

	
	class UsernameWrapper{
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


	@Override
	public void updateUserServiceAccount(User user, boolean isUserServiceAccount) {
		mongoTemplate.updateFirst(query(where(User.ID_FIELD).is(user.getId())), update(User.userServiceAccountField, isUserServiceAccount), User.class);		
	}

	@Override
	public List<User> findByUserInGroup(Collection<String> groups) {
		return findByUniqueField(User.getAdInfoField(String.format("%s.%s",UserAdInfo.groupsField,UserAdInfo.adDnField)), groups);
	}

	@Override
	public void updateAdministratorAccount(User user, boolean isAdministratorAccount) {
		mongoTemplate.updateFirst(query(where(User.ID_FIELD).is(user.getId())), update(User.administratorAccountField, isAdministratorAccount), User.class);
	}
	
	@Override
	public void updateCurrentUserScore(User user, String classifierId, double score, double trendScore, DateTime calculationTime){
		Update update = new Update();
		update.set(User.getClassifierScoreCurrentScoreField(classifierId), score);
		update.set(User.getClassifierScoreCurrentTrendScoreField(classifierId), trendScore);
		update.set(User.getClassifierScoreCurrentTimestampField(classifierId), calculationTime.toDate());
		update.set(User.getClassifierScoreCurrentTimestampEpochField(classifierId), calculationTime.getMillis());
	}
}
