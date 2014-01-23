package fortscale.domain.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;

import fortscale.domain.core.Notification;
import fortscale.domain.core.NotificationAggregate;

public class NotificationsRepositoryImpl implements NotificationsRepositoryCustom {
  private static final long OLD_EVENTS_THRESHOLD_IN_SEC = 60*60*24 ; // we will opt out older events; @todo move to configuration..

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<NotificationAggregate> findAllAndAggregate(PageRequest request) {
		HashMap<String, List<Notification>> aggMap = new HashMap<String, List<Notification>>(); 
		List<NotificationAggregate> aggNotifications = new ArrayList<>();
		
    long current_unix_time = System.currentTimeMillis( ) / 1000L  ; // in seconds
		Query query = new Query( ).with( request.getSort() );
    query.addCriteria( Criteria.where("ts").gte(  new Long( current_unix_time - OLD_EVENTS_THRESHOLD_IN_SEC ) ) );

		List<Notification> notifications = mongoTemplate.find(query, Notification.class);
		
		for (Notification notification : notifications) {
			String cause = notification.getCause();
			if(aggMap.containsKey(cause) == false){
				aggMap.put(cause, new ArrayList<Notification>());
			}
			
			aggMap.get(cause).add(notification);
		}
		
		for (String key: aggMap.keySet()) {
			NotificationAggregate agg = new NotificationAggregate(aggMap.get(key));
			aggNotifications.add(agg);			
		}
		
		int min = Math.min(aggNotifications.size(), request.getPageSize());
		return aggNotifications.subList(0, min);
	}

}
