package fortscale.domain.fe.dao;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.SerializationUtils.serializeToJsonSafely;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation.ProjectionOperationBuilder;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import fortscale.domain.fe.ADFeature;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.FeatureWriteConverter;
import fortscale.domain.fe.IFeature;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.FIProjectionConditionalSubExpression;
import fortscale.utils.mongodb.FIProjectionExpression;
import fortscale.utils.mongodb.FProjectionConditionalExpression;
import fortscale.utils.mongodb.FProjectionConditionalSubExpressionCmp;
import fortscale.utils.mongodb.FProjectionConditionalSubExpressionRefValue;
import fortscale.utils.mongodb.FProjectionConditionalSubExpressionSimpleValue;
import fortscale.utils.mongodb.FProjectionOperation;
import fortscale.utils.mongodb.FProjectionSimpleExpression;

public class AdUsersFeaturesExtractionRepositoryImpl implements	AdUsersFeaturesExtractionRepositoryCustom {
	private static Logger logger = Logger.getLogger(AdUsersFeaturesExtractionRepositoryImpl.class);

	@Autowired
	private MongoDbFactory mongoDbFactory;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void saveMap(AdUserFeaturesExtraction adUsersFeaturesExtraction) {
		DBCollection collection = getDBCollection();

		BasicDBObject basicDBObject = new BasicDBObject(AdUserFeaturesExtraction.classifierIdField, adUsersFeaturesExtraction.getClassifierId());
		basicDBObject.append(AdUserFeaturesExtraction.userIdField, adUsersFeaturesExtraction.getUserId());
		basicDBObject.append(AdUserFeaturesExtraction.scoreField, adUsersFeaturesExtraction.getScore());
		basicDBObject.append(AdUserFeaturesExtraction.timestampField, adUsersFeaturesExtraction.getTimestamp());
		BasicDBList basicDBList = new BasicDBList();
		FeatureWriteConverter converter = new FeatureWriteConverter();
		for(IFeature adFeature: adUsersFeaturesExtraction.getAttrVals()){
			BasicDBObject featureObject = new BasicDBObject();
			featureObject.append(ADFeature.DISPLAY_NAME_FIELD, adFeature.getFeatureDisplayName());
			featureObject.append(ADFeature.FEATURE_SCORE_FIELD, adFeature.getFeatureScore());
			featureObject.append(ADFeature.FEATURE_VALUE_FIELD, adFeature.getFeatureValue());
			basicDBList.add(converter.convert(adFeature));
			basicDBObject.append(adFeature.getFeatureUniqueName(), featureObject);
		}
		basicDBObject.append(AdUserFeaturesExtraction.attrListField, basicDBList);
		
		collection.insert(basicDBObject, WriteConcern.SAFE);
	}

	private DBCollection getDBCollection(){
		DB db = mongoDbFactory.getDb();
		return db.getCollection(AdUserFeaturesExtraction.collectionName);
	}
	
	public Double calculateAvgScore(String classifierId, Date timestamp){
		Aggregation agg = newAggregation(match(where(AdUserFeaturesExtraction.classifierIdField).is(classifierId).andOperator(where(AdUserFeaturesExtraction.timestampField).is(timestamp))),
				project(AdUserFeaturesExtraction.timestampField, AdUserFeaturesExtraction.scoreField),
				group(AdUserFeaturesExtraction.timestampField).avg(AdUserFeaturesExtraction.scoreField).as("score"));
	
		AggregationResults<TimeStampAvgScore> result = mongoTemplate.aggregate(agg, AdUserFeaturesExtraction.collectionName, TimeStampAvgScore.class);
		TimeStampAvgScore ret = result.getMappedResults().get(0);
		return ret.score;
	}
	
	public Double calculateUsersDailyMaxScores(String classifierId, String userId){
		Aggregation agg = newAggregation(match(where(AdUserFeaturesExtraction.classifierIdField).is(classifierId).andOperator(where(AdUserFeaturesExtraction.userIdField).is(userId))),
				 new ProjectionOperationBuilder(AdUserFeaturesExtraction.timestampField, project(AdUserFeaturesExtraction.scoreField), null).project("dayOfYear"),
				group(AdUserFeaturesExtraction.timestampField).max(AdUserFeaturesExtraction.scoreField).as("score"));
	
		AggregationResults<TimeStampAvgScore> result = mongoTemplate.aggregate(agg, AdUserFeaturesExtraction.collectionName, TimeStampAvgScore.class);
		TimeStampAvgScore ret = result.getMappedResults().get(0);
		return ret.score;
	}
	
	class TimeStampAvgScore{
		Date id;
		Double score;
	}
	
	
	public List<ValueSeperator> calculateNumOfUsersWithScoresGTValueSortByTimestamp(String classifierId,List<ValueSeperator> seperators){
		List<FIProjectionExpression> expressions = new ArrayList<FIProjectionExpression>();
		expressions.add(new FProjectionSimpleExpression(AdUserFeaturesExtraction.timestampField));
		
		FProjectionConditionalSubExpressionRefValue scoreRef = new FProjectionConditionalSubExpressionRefValue(AdUserFeaturesExtraction.scoreField);
		FProjectionConditionalSubExpressionSimpleValue trueCase = new FProjectionConditionalSubExpressionSimpleValue(1);
		FProjectionConditionalSubExpressionSimpleValue falseCase = new FProjectionConditionalSubExpressionSimpleValue(0);
		for(ValueSeperator seperator: seperators){
			FProjectionConditionalSubExpressionSimpleValue sepValue = new FProjectionConditionalSubExpressionSimpleValue(seperator.getValue());
			FIProjectionConditionalSubExpression boolExp = FProjectionConditionalSubExpressionCmp.generateGTECmp(scoreRef, sepValue);
			FProjectionConditionalExpression condExp = new FProjectionConditionalExpression(boolExp, trueCase, falseCase);
			expressions.add(new FProjectionSimpleExpression(seperator.getName(), condExp.toDBObject(null)));
		}
		
		FProjectionOperation fProjectionOperation = new FProjectionOperation(expressions);
		GroupOperation groupOperation = group(AdUserFeaturesExtraction.timestampField);
		for(ValueSeperator seperator: seperators){
			groupOperation = groupOperation.sum(seperator.getName()).as(String.format("%s", seperator.getName()));
		}
		Aggregation agg = newAggregation(
				match(where(AdUserFeaturesExtraction.classifierIdField).is(classifierId)),
				fProjectionOperation,
				groupOperation,
				sort(DESC,"_id"),
				limit(1));
		
		AggregationOperationContext rootContext = Aggregation.DEFAULT_CONTEXT;
		DBObject command = agg.toDbObject(AdUserFeaturesExtraction.collectionName, rootContext);
		logger.debug("Executing aggregation: {}", serializeToJsonSafely(command));
		CommandResult commandResult = mongoTemplate.executeCommand(command);
		handleCommandError(commandResult, command);

		// map results
		@SuppressWarnings("unchecked")
		Iterable<DBObject> resultSet = (Iterable<DBObject>) commandResult.get("result");
		if(resultSet.iterator().hasNext()){
			DBObject res = resultSet.iterator().next();
			for(ValueSeperator seperator: seperators){
				seperator.setCount(Integer.parseInt(res.get(seperator.getName()).toString()));
			}
		}
		return seperators;
		
	}
	
	
	/**
	 * Inspects the given {@link CommandResult} for erros and potentially throws an
	 * {@link InvalidDataAccessApiUsageException} for that error.
	 * 
	 * @param result must not be {@literal null}.
	 * @param source must not be {@literal null}.
	 */
	private void handleCommandError(CommandResult result, DBObject source) {

		try {
			result.throwOnError();
		} catch (MongoException ex) {

			String error = result.getErrorMessage();
			error = error == null ? "NO MESSAGE" : error;

			throw new InvalidDataAccessApiUsageException("Command execution failed:  Error [" + error + "], Command = "
					+ source, ex);
		}
	}
}
