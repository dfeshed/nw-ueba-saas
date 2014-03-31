package fortscale.domain.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import fortscale.utils.TimestampUtils;

public class NotificationsRepositoryImpl implements NotificationsRepositoryCustom {

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

		Query query = new Query( ).with( request.getSort() );
		query.fields().exclude("comments");
		query.addCriteria(new Criteria().orOperator(Criteria.where("dismissed").is(false), Criteria.where("dismissed").exists(false)));
		query.limit(request.getPageSize());

		int numAggregatesFound = 0;
		int pageNum = 0;
		boolean hasMoreData = true;
		while (numAggregatesFound < request.getPageSize() && hasMoreData) {
			// get the next page of notifications from mongo
			query.skip(pageNum * request.getPageSize());
			List<Notification> notifications = mongoTemplate.find(query, Notification.class);
			
			// build aggregates from the returned notifications
			for (Notification notification : notifications) {
				String cause = notification.getCause();
				if (!aggMap.containsKey(cause)) {
					if (numAggregatesFound==request.getPageSize()) {
						// reach enough notifications aggregates, skip this notification
						continue;
					}
					
					aggMap.put(cause, new ArrayList<Notification>());
					numAggregatesFound++;
				}
				aggMap.get(cause).add(notification);
			}
			
			// check if we received less data than expected, hence no more data
			if (notifications.size()<request.getPageSize())
				hasMoreData = false;
			
			// advance to the next page
			pageNum++;
		}
		
		// build aggregates from the notifications received
		for (String key: aggMap.keySet()) {
			NotificationAggregate agg = new NotificationAggregate(aggMap.get(key));
			aggNotifications.add(agg);
		}
		
		// ensure we return only the first notifications requested
		int min = Math.min(aggNotifications.size(), request.getPageSize());
		return aggNotifications.subList(0, min);
	}

	
	@Override
	public Page<Notification> findByPredicates(List<String> includeFsID, List<String> excludeFsID, boolean includeDissmissed, 
			List<String> includeGenerators, List<String> excludeGenerators, long before, long after, PageRequest request) {
		
		// build query object with the criterias
		Query query = new Query();
		if (includeFsID!=null && !includeFsID.isEmpty())
			query.addCriteria(Criteria.where("fsId").in(includeFsID));
		if (excludeFsID!=null && !excludeFsID.isEmpty())
			query.addCriteria(Criteria.where("fsId").not().in(excludeFsID));
		if (!includeDissmissed)
			query.addCriteria(new Criteria().orOperator(Criteria.where("dismissed").is(false), Criteria.where("dismissed").exists(false)));
		if (includeGenerators!=null && !includeGenerators.isEmpty())
			query.addCriteria(Criteria.where("generator_name").in(includeGenerators));
		if (excludeGenerators!=null && !excludeGenerators.isEmpty())
			query.addCriteria(Criteria.where("generator_name").not().in(excludeGenerators));
		
		if (before!=0L && after!=0L) {
			query.addCriteria(Criteria.where("ts").lte(TimestampUtils.convertToSeconds(before)).gte(TimestampUtils.convertToSeconds(after)));
		} else {
			if (before!=0L)
				query.addCriteria(Criteria.where("ts").lte(TimestampUtils.convertToSeconds(before)));
			if (after!=0L)
				query.addCriteria(Criteria.where("ts").gte(TimestampUtils.convertToSeconds(after)));
		}
		
		
		// set paging and sort parameters
		query.with(request);
		
		// exclude comments
		query.fields().exclude("comments");
		
		long total = mongoTemplate.count(query, Notification.class);
		List<Notification> notifications = mongoTemplate.find(query, Notification.class);
		
		return new PageImpl<Notification>(notifications, request, total);
	}
}
