
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;


@Component("mongoDbRepositoryUtil")
public class MongoDbRepositoryUtil {

	@Autowired
	private MongoTemplate mongoTemplate;

	public String getLatestTimeStampString(String timestampField, String collectionName) {

		Aggregation agg = newAggregation(project(timestampField), group(timestampField), sort(DESC, "_id"), limit(1));

		AggregationResults<AdUserTimeStampString> result =
			mongoTemplate.aggregate(agg, collectionName, AdUserTimeStampString.class);
		if (result.getMappedResults().isEmpty()) {
			return null;
		}
		AdUserTimeStampString ret = result.getMappedResults().get(0);
		return ret.id;
	}

	class AdUserTimeStampString {

		String id;
//		String timestamp;
	}

	public Date getLatestTimeStampDate(String timestampField, String collectionName) {

		Aggregation agg = newAggregation(project(timestampField), group(timestampField), sort(DESC, "_id"), limit(1));

		AggregationResults<AdUserTimeStampDate> result =
			mongoTemplate.aggregate(agg, collectionName, AdUserTimeStampDate.class);
		if (result.getMappedResults().isEmpty()) {
			return null;
		}
		AdUserTimeStampDate ret = result.getMappedResults().get(0);
		return ret.id;
	}

	class AdUserTimeStampDate {

		Date id;
//		String timestamp;
	}
	
	public <T> Page<T> getPage(Query query, Pageable pageable, Class<T> entityClass, boolean countTotal){
		query.with(pageable);
		List<T> content = mongoTemplate.find(query, entityClass);
		if(countTotal){
		long total = mongoTemplate.count(query, entityClass);
		return new PageImpl<>(content, pageable, total);
		}else{
			return new PageImpl<>(content);
		}
	}
	
}
