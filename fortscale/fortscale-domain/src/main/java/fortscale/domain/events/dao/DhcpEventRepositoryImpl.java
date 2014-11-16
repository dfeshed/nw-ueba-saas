package fortscale.domain.events.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.events.DhcpEvent;

public class DhcpEventRepositoryImpl implements DhcpEventRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public DhcpEvent findLatestEventForComputerBeforeTimestamp(String ipAddress, String hostname, long timestamp) {
		// construct query object
		Query query = new Query();
		query.addCriteria(where(DhcpEvent.IP_ADDRESS_FIELD_NAME).is(ipAddress));
		query.addCriteria(where(DhcpEvent.HOSTNAME_FIELD_NAME).is(hostname));
		query.addCriteria(where(DhcpEvent.TIMESTAMP_EPOCH_FIELD_NAME).lte(timestamp));
		query.limit(1);
		query.with(new Sort(Direction.DESC, DhcpEvent.TIMESTAMP_EPOCH_FIELD_NAME));
		
		// execute the query
		List<DhcpEvent> events = mongoTemplate.find(query, DhcpEvent.class);
		return (events.isEmpty())? null : events.get(0);
	}

	

}
