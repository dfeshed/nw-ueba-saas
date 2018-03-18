
package fortscale.domain.core.dao;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.util.*;

import com.mongodb.BasicDBObject;
import fortscale.domain.core.Alert;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;


@Component("mongoDbRepositoryUtil")
public class MongoDbRepositoryUtil {

	public static  String TOTAL_FIELD_NAME = "total";

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

	/**
	 * Execute the aggregation query, and build the results map
	 * @param fieldName
	 * @param agg
	 * @return
	 */
	private Map<String, Integer> getAggregationResultMap(String fieldName, Aggregation agg,
														String collection) {
		AggregationResults<BasicDBObject> groupResults
				= mongoTemplate.aggregate(agg, collection, BasicDBObject.class);

		//Convert the aggregation result into a map of "key = fieldValue, value= field count"
		Map<String, Integer> results = new HashMap<>();
		for (BasicDBObject item:  groupResults.getMappedResults()) {
			String fieldValue = item.get(fieldName).toString();
			String countAsString = item.get(TOTAL_FIELD_NAME).toString();

			int count;
			if (StringUtils.isBlank(countAsString)){
				count = 0;
			} else {
				count = Integer.parseInt(countAsString);
			}
			results.put(fieldValue,count);
		}
		return results;
	}


	/**
	 * "Select count by" on collection, according to given field name
	 * @param fieldName
	 * @param collectionName
	 * @return map of the field value to how many instances this value aprears in the collection
	 */
	public Map<String, Integer> groupCount(String fieldName,  String collectionName){
		return groupCount(fieldName, null, collectionName);
	}

	/**
	 * "Select count by" on collection, according to given criteria and field name
	 * @param fieldName
	 * @param criteria
	 * @param collectionName
	 * @return map of the field value to how many instances this value aprears in the collection
	 */
	public Map<String, Integer> groupCount(String fieldName, Criteria criteria, String collectionName){

		Aggregation agg;
		if (criteria!=null){
			//Create aggregation on fieldName, for all alerts according to filter
			agg = Aggregation.newAggregation(
					match(criteria),
					group(fieldName).count().as(TOTAL_FIELD_NAME),
					project(TOTAL_FIELD_NAME).and(fieldName).previousOperation());
		} else {
			//Create aggregation on fieldName, for all alerts without filter
			agg = Aggregation.newAggregation(
					group(fieldName).count().as(TOTAL_FIELD_NAME),
					project(TOTAL_FIELD_NAME).and(fieldName).previousOperation());
		}

		Map<String, Integer> results = this.getAggregationResultMap(fieldName, agg, collectionName);
		//Return the map
		return results;
	}

	public Map<Pair<String,String>, Integer>  groupCountBy2Fields(String fieldName1, String fieldName2, Criteria criteria, String collectionName){

		//Create aggregation with or without condition
		Aggregation agg;
		if (criteria!=null) {
			agg = newAggregation(
					match(criteria),
					project("count")
							.andExpression(fieldName1).as(fieldName1)
							.andExpression(fieldName2).as(fieldName2),
					group(fields().and(fieldName1).and(fieldName2))
							.count().as("count")
			);
		} else {
			agg = newAggregation(
					project("count")
							.andExpression(fieldName1).as(fieldName1)
							.andExpression(fieldName2).as(fieldName2),
					group(fields().and(fieldName1).and(fieldName2))
							.count().as("count")
			);
		}

		//Retrieve result
		AggregationResults<BasicDBObject> groupResults
				= mongoTemplate.aggregate(agg, Alert.COLLECTION_NAME, BasicDBObject.class);

		//Parse result into map of the two values (field1, field2) and the count
		Map<Pair<String,String>, Integer> results = new HashMap<>();

		for (BasicDBObject dbObject : groupResults.getMappedResults()) {
			String field1Value = (String)dbObject.get(fieldName1);
			String field2Value = (String)dbObject.get(fieldName2);
			Integer count = (Integer)dbObject.get("count");

			Pair<String,String> key = new ImmutablePair<>(field1Value,field2Value);
			results.put(key,count);
		}

		return results;

	}



}
