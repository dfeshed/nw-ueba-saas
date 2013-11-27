package fortscale.domain.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.core.Notification;
import fortscale.domain.core.NotificationAggregate;

public class NotificationsRepositoryImpl implements NotificationsRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<Object> findAllAndAggregate(PageRequest request) {
		HashMap<String, List<Notification>> aggMap = new HashMap<String, List<Notification>>(); 
		List<Object> aggNotifications = new ArrayList<>();
		
		Query query = new Query().with(request.getSort());
		List<Notification> notifications = mongoTemplate.find(query, Notification.class);
		
		for (Notification notification : notifications) {
			String cause = notification.getCause();
			if(aggMap.containsKey(cause) == false){
				aggMap.put(cause, new ArrayList<Notification>());
			}
			
			aggMap.get(cause).add(notification);
		}
		
		for (String key: aggMap.keySet()) {
			List<Notification> list = aggMap.get(key);
			if(list.size() == 1){
				aggNotifications.add(list.get(0));
			}else{
				NotificationAggregate agg = new NotificationAggregate(list);
				aggNotifications.add(agg);
			}
		}
		
		int min = Math.min(aggNotifications.size(), request.getPageSize());
		return aggNotifications.subList(0, min);
	}

}
