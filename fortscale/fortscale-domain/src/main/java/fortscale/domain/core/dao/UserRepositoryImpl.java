package fortscale.domain.core.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
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
	public List<User> findByClassifierIdAndScoreBetween(String classifierId, int lowestVal, int upperVal, Pageable pageable) {
		String classifierCurScoreField = User.getClassifierScoreCurrentScoreField(classifierId);
		Query query = new Query(where(classifierCurScoreField).gte(lowestVal).lt(upperVal));
		query.with(pageable);
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public int countNumOfUsersAboveThreshold(String classifierId, Threshold threshold) {
		String classifierCurScoreField = User.getClassifierScoreCurrentScoreField(classifierId);
		Query query = new Query(where(classifierCurScoreField).gte(threshold.getValue()));
		return (int) mongoTemplate.count(query, User.class);
	}
	
}
