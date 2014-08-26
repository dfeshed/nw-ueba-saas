package fortscale.domain.core.dao;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
@Component("mongoDbRepositoryUtil")
public class MongoDbRepositoryUtil {
	
	@Autowired
	static private MongoTemplate mongoTemplate;
	
	public String getLatestTimeStampString(String timestampField, String collectionName) {
		Aggregation agg = newAggregation(project(timestampField),
				group(timestampField),
				sort(DESC,"_id"),
				limit(1));
	
		AggregationResults<AdUserTimeStampString> result = mongoTemplate.aggregate(agg, collectionName, AdUserTimeStampString.class);
		if(result.getMappedResults().isEmpty()) {
			return null;
		}
		AdUserTimeStampString ret = result.getMappedResults().get(0);
		return ret.id;
	}
	
	class AdUserTimeStampString{
		String id;
//		String timestamp;
	}
	
	static public Date getLatestTimeStampDate(String timestampField, String collectionName) {
		Query query = new Query();
		query.with(new Sort(Sort.Direction.DESC, timestampField)).limit(1);
		query.fields().include(timestampField);
		List<AdUserTimeStampDate> result = mongoTemplate.find(query, AdUserTimeStampDate.class, collectionName);
		if(result.isEmpty()) {
			return null;
		}
		AdUserTimeStampDate ret = result.get(0);
		return ret.id;
	}
	
	class AdUserTimeStampDate{
		Date id;
//		String timestamp;
	}
}
