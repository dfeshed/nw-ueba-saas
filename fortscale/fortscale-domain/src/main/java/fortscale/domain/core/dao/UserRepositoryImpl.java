package fortscale.domain.core.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;


import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;

public class UserRepositoryImpl implements UserRepositoryCustom{
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public User findByApplicationUserName(ApplicationUserDetails applicationUserDetails) {
		String appUserNameField = String.format("%s.%s.%s", User.appField,applicationUserDetails.getApplicationName(),ApplicationUserDetails.userNameField);
		Query query = new Query(where(appUserNameField).is(applicationUserDetails.getUserName()));
		return mongoTemplate.findOne(query, User.class);
	}
	
}
