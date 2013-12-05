package fortscale.domain.core.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.core.NotificationResource;

public class NotificationResourcesRepositoryImpl implements NotificationResourcesRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	
	@Override
	public NotificationResource findByMsg_name(String msg_name) {
		Query query = new Query(where("msg_name").is(msg_name));
		return mongoTemplate.findOne(query, NotificationResource.class);
	}
	
}

