package fortscale.domain.core.dao;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fortscale.domain.core.DataSourceAnomalyTypePair;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by tomerd on 01/09/2015.
 */
public class EvidencesRepositoryImpl implements EvidencesRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	public List<Evidence> findFeatureEvidences(EntityType entityType, String entityName, long startDate, long endDate,
			String dataEntities, String featureName) {
		Query query = new Query(where
				(Evidence.entityTypeField).is(entityType).and
				(Evidence.entityNameField).is(entityName).and
				(Evidence.startDateField).gte(startDate).and
				(Evidence.endDateField).lte(endDate).and
				(Evidence.dataEntityIdField).is(dataEntities).and
				(Evidence.anomalyTypeFieldNameField).is(featureName)
		);

		return mongoTemplate.find(query, Evidence.class);
	}

	public long countWithParameters(long fromTime, long toTime){
		Query query = new Query(where(Evidence.startDateField).gte(fromTime).lte(toTime));
		Long count = mongoTemplate.count(query, Evidence.class);
		return count;
	}

	/**
	 * Finds all distinct anomaly types to data source.
	 * Output will be <data-source>@@@<anomaly-type>
	 *
	 * @return
     */
	public List<String> getDistinctAnomalyType() {

		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.unwind(Evidence.dataEntityIdField),
				Aggregation.project(Evidence.dataEntityIdField, Evidence.anomalyTypeFieldNameField)
						.andExpression("concat(\"$dataEntitiesIds\", \"@@@\" , \"$anomalyTypeFieldName\")")
						.as("anomalyType"),
				Aggregation.group("anomalyType")
		);

		AggregationResults<DBObject> results = mongoTemplate.aggregate(aggregation, "evidences", DBObject.class);
		List<String> distinctAnomalyTypes = new ArrayList<>();
		results.forEach(dbObject -> distinctAnomalyTypes.add(dbObject.get("_id").toString()));

		return distinctAnomalyTypes;

	}

	@Override
	public List getDistinctByFieldName(String fieldName) {
		return mongoTemplate.getCollection("evidences").distinct(fieldName);
	}


	/**
	 *
	 * @param anomalyTypesList The list od data source id to anomaly type field names list
	 * @return
     */
	/*private Query createEvidenceIdsByAnomalyTypeQuery(List<DataSourceAnomalyTypePair> anomalyTypesList) {

		DBObject orCondition = new BasicDBObject();
		BasicDBList orList = new BasicDBList();

		try {
			anomalyTypesList.forEach(anomalyTypeItem -> {

				// Break down into data source id and anomaly type field name
				String dataSource = anomalyTypeItem.getDataSourceId();
				DBObject andCondition = new BasicDBObject();
				BasicDBList andList = new BasicDBList();


				// Add data source condition
				andList.add(new BasicDBObject(Evidence.dataEntityIdField + ".0", dataSource));

				// Create and add the $in condition
				if (anomalyTypeItem.getAnomalyTypes() != null && anomalyTypeItem.getAnomalyTypes().size() > 0) {
					String[] anomalyTypeFieldsNames = anomalyTypeItem.getAnomalyTypes()
							.toArray(new String[anomalyTypeItem.getAnomalyTypes().size()]);
					// Create the $in condition
					andList.add(new BasicDBObject(
							Evidence.anomalyTypeFieldNameField,
							new BasicDBObject(
									"$in", anomalyTypeFieldsNames
							)));
				}

				// Create the $and condition
				andCondition.put("$and", andList);
				orList.add(andCondition);

			});
		} catch (RuntimeException e) {
			return null;
		}

		// Populate the $or condition
		orCondition.put("$or", orList);

		// Create the query
		Query query = new BasicQuery(orCondition);
		query.fields().include(Evidence.ID_FIELD);

		return query;

	}*/

//	@Override
//	public List<String> getEvidenceIdsByAnomalyTypeFiledNames(List<DataSourceAnomalyTypePair> anomalyTypesList) {
//
//
//		if (anomalyTypesList == null || anomalyTypesList.size() == 0) {
//			return null;
//		}
//
//		Query query = createEvidenceIdsByAnomalyTypeQuery(anomalyTypesList);
//		// Get the evidences
//		List<Evidence> indicators = mongoTemplate.find(query, Evidence.class);
//		List<String> ids = new ArrayList<>();
//
//		// Populate ids list
//		indicators.forEach(indicator -> ids.add(indicator.getId()));
//
//		return ids;
//
//	}
//
//	;



}
