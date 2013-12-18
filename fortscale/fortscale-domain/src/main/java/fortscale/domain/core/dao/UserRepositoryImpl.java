package fortscale.domain.core.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.core.UserAdInfo;
import fortscale.domain.fe.dao.Threshold;

public class UserRepositoryImpl implements UserRepositoryCustom{
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public User findByApplicationUserName(ApplicationUserDetails applicationUserDetails) {
		String appUserNameField = User.getAppUserNameField(applicationUserDetails.getApplicationName());
		Query query = new Query(where(appUserNameField).is(applicationUserDetails.getUserName()));
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
		Query query = new Query(where(logUsernameField).regex(String.format("^%s$", username),"i"));
		return mongoTemplate.findOne(query, User.class);
	}

	@Override
	public List<User> findByClassifierIdAndScoreBetween(String classifierId, int lowestVal, int upperVal, Pageable pageable) {
		DateTime dateTime = new DateTime();
		dateTime = dateTime.withTimeAtStartOfDay();
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		String classifierCurScoreField = User.getClassifierScoreCurrentScoreField(classifierId);
		Query query = new Query(where(classifierCurScoreField).gte(lowestVal).lt(upperVal).and(classifierScoreCurrentTimestampField).gte(dateTime.toDate()));
		query.with(pageable);
		return mongoTemplate.find(query, User.class);
	}
	
	@Override
	public List<User> findByClassifierIdAndFollowedAndScoreBetween(String classifierId, int lowestVal, int upperVal, Pageable pageable) {
		DateTime dateTime = new DateTime();
		dateTime = dateTime.withTimeAtStartOfDay();
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		String classifierCurScoreField = User.getClassifierScoreCurrentScoreField(classifierId);
		Query query = new Query(where(User.followedField).is(true).and(classifierCurScoreField).gte(lowestVal).lt(upperVal).and(classifierScoreCurrentTimestampField).gte(dateTime.toDate()));
		query.with(pageable);
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public int countNumOfUsersAboveThreshold(String classifierId, Threshold threshold) {
		DateTime dateTime = new DateTime();
		dateTime = dateTime.withTimeAtStartOfDay();
		String classifierScoreCurrentTimestampField = User.getClassifierScoreCurrentTimestampField(classifierId);
		String classifierCurScoreField = User.getClassifierScoreCurrentScoreField(classifierId);
		Query query = new Query(where(classifierCurScoreField).gte(threshold.getValue()).and(classifierScoreCurrentTimestampField).gte(dateTime.toDate()));
		return (int) mongoTemplate.count(query, User.class);
	}

	@Override
	public int countNumOfUsers(String classifierId) {
		DateTime dateTime = new DateTime();
		dateTime = dateTime.withTimeAtStartOfDay();
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
	
	private List<User> findByUniqueField(String fieldName, Collection<?> vals) {
		Criteria criterias[] = new Criteria[vals.size()];
		int i = 0;
		for(Object val: vals){
			criterias[i] = where(fieldName).is(val);
			i++;
		}
		Query query = new Query(new Criteria().orOperator(criterias));
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public List<User> findByIds(Collection<String> ids) {
		return findByUniqueField(User.ID_FIELD,ids);
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
	public User findByAdObjectGUID(String objectGUID) {
		return findOneByField(User.getAdInfoField(UserAdInfo.objectGUIDField), objectGUID);
	}
	
	private List<User> findByField(String field, Object val){
		List<String> fields = new ArrayList<>();
		fields.add(field);
		List<Object> vals = new ArrayList<>();
		vals.add(val);
		return findByFields(fields, vals);
	}
	
	private User findOneByField(String field, Object val){
		List<User> users = findByField(field, val);
		return users.get(0);
	}

	private List<User> findByFields(List<String> fields, List<?> vals){
		Criteria criteria = where(fields.get(0)).is(vals.get(0));
		for(int i = 1; i < fields.size(); i++){
			criteria.and(fields.get(i)).is(vals.get(i));
		}
		Query query = new Query(criteria);
		return mongoTemplate.find(query, User.class);
	}

	
}
