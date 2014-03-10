package fortscale.domain.core.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
	public List<Notification> findByFsIdExcludeComments(String fsid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("fsId").is(fsid));
		query.with(new Sort(Direction.DESC, "ts")).limit(10);
		query.fields().exclude("comments");
		return mongoTemplate.find(query, Notification.class);
	}
	
	@Override
	public List<Notification> findByTsGreaterThanExcludeComments(long ts, Sort sort) {
		Query query = new Query();
		query.addCriteria(Criteria.where("ts").gt(ts));
		query.with(sort);
		query.fields().exclude("comments");
		return mongoTemplate.find(query, Notification.class);
	}
	
	@Override
	public List<NotificationAggregate> findAllAndAggregate(PageRequest request) {
		HashMap<String, List<Notification>> aggMap = new HashMap<String, List<Notification>>(); 
		List<NotificationAggregate> aggNotifications = new ArrayList<>();
		
		long current_unix_time = System.currentTimeMillis( ) / 1000L  ; // in seconds
		Query query = new Query( ).with( request.getSort() );
		query.fields().exclude("comments");
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

	
	@Override
	public Page<Notification> findByPredicates(Set<String> includeFsID, Set<String> excludeFsID, boolean includeDissmissed, 
			Set<String> includeGenerators, Set<String> excludeGenerators, Date before, Date after, PageRequest request) {
		
		// build query object with the criterias
		Query query = new Query();
		if (includeFsID!=null && !includeFsID.isEmpty())
			query.addCriteria(Criteria.where("fsId").in(includeFsID));
		if (excludeFsID!=null && !excludeFsID.isEmpty())
			query.addCriteria(Criteria.where("fsId").not().in(excludeFsID));
		if (!includeDissmissed)
			query.addCriteria(Criteria.where("dismissed").is(false).orOperator(Criteria.where("dismissed").exists(false)));
		if (includeGenerators!=null && !includeGenerators.isEmpty())
			query.addCriteria(Criteria.where("generator_name").in(includeGenerators));
		if (excludeGenerators!=null && !includeGenerators.isEmpty())
			query.addCriteria(Criteria.where("generator_name").not().in(excludeGenerators));
		if (before!=null)
			query.addCriteria(Criteria.where("ts").lte(before.getTime() / 1000L));
		if (after!=null)
			query.addCriteria(Criteria.where("ts").gte(after.getTime() / 1000L));
		
		// set paging and sort parameters
		query.with(request);
		
		// exclude comments
		query.fields().exclude("comments");
		
		long total = mongoTemplate.count(query, Notification.class);
		List<Notification> notifications = mongoTemplate.find(query, Notification.class);
		
		return new PageImpl<Notification>(notifications, request, total);
	}
}
