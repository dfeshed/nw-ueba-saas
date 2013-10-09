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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;

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
		
		try {

			BasicDBObject basicDBObject = new BasicDBObject(AdUserFeaturesExtraction.classifierIdField, adUsersFeaturesExtraction.getClassifierId());
			basicDBObject.append(AdUserFeaturesExtraction.userIdField, adUsersFeaturesExtraction.getUserId());
			basicDBObject.append(AdUserFeaturesExtraction.scoreField, adUsersFeaturesExtraction.getScore());
			basicDBObject.append(AdUserFeaturesExtraction.timestampField, adUsersFeaturesExtraction.getTimestamp());
			BasicDBList basicDBList = new BasicDBList();
			FeatureWriteConverter converter = new FeatureWriteConverter();
			for(IFeature adFeature: getValidFeatures(adUsersFeaturesExtraction)){
				try {
				BasicDBObject featureObject = new BasicDBObject();
				featureObject.append(ADFeature.DISPLAY_NAME_FIELD, adFeature.getFeatureDisplayName());
				featureObject.append(ADFeature.FEATURE_SCORE_FIELD, adFeature.getFeatureScore());
				featureObject.append(ADFeature.FEATURE_VALUE_FIELD, adFeature.getFeatureValue());
				
				basicDBObject.append(getFeatureDbFieldNameString(adFeature), featureObject);
				} catch(Exception e) {
					logger.error("failed to add the attribute as a field to the db" , e);
					logger.error("the attribute field name was {}", getFeatureDbFieldNameString(adFeature));
				}
				try {
					basicDBList.add(converter.convert(adFeature));
				} catch (Exception e) {
					logger.error("failed to add the attribute to the attributes list." , e);
				}
				
			}
			basicDBObject.append(AdUserFeaturesExtraction.attrListField, basicDBList);
			
			collection.insert(basicDBObject, WriteConcern.SAFE);
		
		} catch (Exception e) {
			logger.error("Got the following exception when trying to save AdUserFeaturesExtraction",e);
		}
	}
	
	private List<IFeature> getValidFeatures(AdUserFeaturesExtraction adUsersFeaturesExtraction) {
		List<IFeature> features = adUsersFeaturesExtraction.getAttributes();
		List<IFeature> retFeatures = new ArrayList<>();
		Set<String> uniqueNameSet = new HashSet<>();
		Set<String> invalidUniqueNameSet = new HashSet<>();
		for(IFeature feature: features) {
			if (uniqueNameSet.contains(feature.getFeatureUniqueName())) {
				invalidUniqueNameSet.add(feature.getFeatureUniqueName());
				logger.error("The following unique name exist in more than one feature. unique name: {}", feature.getFeatureUniqueName());
			} else {
				uniqueNameSet.add(feature.getFeatureUniqueName());
			}
		}for(IFeature feature: features) {
			if(StringUtils.isEmpty(feature.getFeatureUniqueName())) {
				logError("feature unique name is empty.", adUsersFeaturesExtraction);
				continue;
			}
			if(StringUtils.isEmpty(feature.getFeatureDisplayName())) {
				logError("feature display name is empty.", adUsersFeaturesExtraction);
				continue;
			}
			if(invalidUniqueNameSet.contains(feature.getFeatureUniqueName())) {
				continue;
			}
			
			if(feature.getFeatureScore() == null) {
				logError("feature score is null.", adUsersFeaturesExtraction);
				continue;
			}
			if (feature.getFeatureValue() == null) {
				logError("feature value is null.", adUsersFeaturesExtraction);
				continue;
			}
			retFeatures.add(feature);
		}
		return retFeatures;
	}
	
	private void logError(String msg, AdUserFeaturesExtraction adUsersFeaturesExtraction) {
		logger.error("{} info: id ({}), classifier id ({}), raw id ({}), user id ({}), timestamp ({})", msg,
				adUsersFeaturesExtraction.getId(), adUsersFeaturesExtraction.getClassifierId()
				,adUsersFeaturesExtraction.getRawId(), adUsersFeaturesExtraction.getUserId(), adUsersFeaturesExtraction.getTimestamp());
	}
	
	private String getFeatureDbFieldNameString(IFeature feature) {
		String retString = feature.getFeatureUniqueName().replace('.', ' ');
		if(retString.startsWith("$")) {
			retString = String.format("fortscalestart_%s", retString);
		}
		return retString;
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
	
//	public Double calculateUsersDailyMaxScores(String classifierId, String userId){
//		Aggregation agg = newAggregation(match(where(AdUserFeaturesExtraction.classifierIdField).is(classifierId).andOperator(where(AdUserFeaturesExtraction.userIdField).is(userId))),
//				 new ProjectionOperationBuilder(AdUserFeaturesExtraction.timestampField, project(AdUserFeaturesExtraction.scoreField), null).project("dayOfYear"),
//				group(AdUserFeaturesExtraction.timestampField).max(AdUserFeaturesExtraction.scoreField).as("score"));
//	
//		AggregationResults<TimeStampAvgScore> result = mongoTemplate.aggregate(agg, AdUserFeaturesExtraction.collectionName, TimeStampAvgScore.class);
//		TimeStampAvgScore ret = result.getMappedResults().get(0);
//		return ret.score;
//	}
	
	class TimeStampAvgScore{
		Date id;
		Double score;
	}
	
	
	public List<Threshold> calculateNumOfUsersWithScoresGTThresholdForLastRun(String classifierId,List<Threshold> thresholds){
		//Defining the projection operation to be the timestamp + conditional expression for each Threshold.
		//The conditional threshold will help us to count how much rows we that are bigger or equal to the Threshold.
		List<FIProjectionExpression> expressions = new ArrayList<FIProjectionExpression>();
		expressions.add(new FProjectionSimpleExpression(AdUserFeaturesExtraction.timestampField));
		
		FProjectionConditionalSubExpressionRefValue scoreRef = new FProjectionConditionalSubExpressionRefValue(AdUserFeaturesExtraction.scoreField);
		FProjectionConditionalSubExpressionSimpleValue trueCase = new FProjectionConditionalSubExpressionSimpleValue(1);
		FProjectionConditionalSubExpressionSimpleValue falseCase = new FProjectionConditionalSubExpressionSimpleValue(0);
		for(Threshold threshold: thresholds){
			FProjectionConditionalSubExpressionSimpleValue sepValue = new FProjectionConditionalSubExpressionSimpleValue(threshold.getValue());
			FIProjectionConditionalSubExpression boolExp = FProjectionConditionalSubExpressionCmp.generateGTECmp(scoreRef, sepValue);
			FProjectionConditionalExpression condExp = new FProjectionConditionalExpression(boolExp, trueCase, falseCase);
			expressions.add(new FProjectionSimpleExpression(threshold.getName(), condExp));
		}
		
		FProjectionOperation fProjectionOperation = new FProjectionOperation(expressions);
		
		//Defining the group operation to be grouped by the time stamp and to count how much rows we have for each threshold.
		GroupOperation groupOperation = group(AdUserFeaturesExtraction.timestampField);
		for(Threshold threshold: thresholds){
			groupOperation = groupOperation.sum(threshold.getName()).as(String.format("%s", threshold.getName()));
		}
		
		//Defining the whole aggregation query.
		Aggregation agg = newAggregation(
				match(where(AdUserFeaturesExtraction.classifierIdField).is(classifierId)),
				fProjectionOperation,
				groupOperation,
				sort(DESC,"_id"),
				limit(1));
		
		//Running the query.
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
			for(Threshold seperator: thresholds){
				seperator.setCount(Integer.parseInt(res.get(seperator.getName()).toString()));
			}
		}
		return thresholds;
		
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
