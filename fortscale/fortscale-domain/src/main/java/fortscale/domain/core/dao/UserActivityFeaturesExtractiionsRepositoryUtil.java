
package fortscale.domain.core.dao;

import com.mongodb.BasicDBObject;
import fortscale.domain.core.Alert;
import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityDocument;
import fortscale.domain.core.activities.UserActivityJobState;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static fortscale.utils.time.TimestampUtils.convertToSeconds;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;


@Component("userActivityFeaturesExtractiionsRepositoryUtil")
public class UserActivityFeaturesExtractiionsRepositoryUtil {


	@Autowired
	private MongoTemplate mongoTemplate;

	protected final Logger logger = Logger.getLogger(this.getClass());

	/**
	 * Get a list of all context id between start and end time from the relevant collection
 	 * @param startTime
	 * @param endTime
	 * @param startTimeFieldName
	 * @param endTimeFieldName
	 * @param collectionName
	 * @return
	 */
	public List<String> getContextIdList(long startTime, long endTime, String startTimeFieldName, String endTimeFieldName, String collectionName) {
		Criteria startTimeCriteria = Criteria.where(startTimeFieldName).gte(TimestampUtils.convertToSeconds(startTime));
		Criteria endTimeCriteria = Criteria.where(endTimeFieldName).lte(TimestampUtils.convertToSeconds(endTime));
		Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria));

		return mongoTemplate.getCollection(collectionName).distinct("contextId", query.getQueryObject());
	}


	/**
	 *
	 * @param startTime
	 * @param endTime
	 * @param usersChunk - list of user relevant
	 * @param collectionName - the name of the collection
	 * @param relevantFields - list of required features
	 * @param contextIdField - the context id (include the username)
	 * @param startTimefield
	 * @param featuresClass - the histrogram class
	 * @param <T>
	 * @return
	 */
	public <T> List<T> getFeatureBuckets(long startTime,
												 long endTime,
												 List<String> usersChunk,
												 String collectionName,
												 List<String> relevantFields,
												 String contextIdField,
												 String startTimefield,
												 Class<T> featuresClass) {
		Query query = new Query();
		query.addCriteria(where(contextIdField).in(usersChunk));
		query.addCriteria(where(startTimefield)
				.gte(convertToSeconds(startTime))
				.lt(convertToSeconds(endTime)));
		query.fields().include(contextIdField);

		relevantFields.forEach(field -> query.fields().include(field));


		return mongoTemplate.find(query, featuresClass, collectionName);
	}

	protected void removeRelevantDocuments(Object startingTime, List<Class> relatedDocuments) {
		for (Class relatedDocumentClass : relatedDocuments) {
			Query query = new Query();
			query.addCriteria(Criteria.where(UserActivityDocument.START_TIME_FIELD_NAME).lt(startingTime));
			mongoTemplate.remove(query, relatedDocumentClass);
		}
	}

	public UserActivityJobState loadAndUpdateJobState(String activityName, int numOfLastDaysToCalculate, List<Class> relatedDocuments) {
		Criteria criteria = Criteria.where(UserActivityJobState.ACTIVITY_NAME_FIELD).is(activityName);

		Query query = new Query(criteria);
		UserActivityJobState userActivityJobState = mongoTemplate.findOne(query, UserActivityJobState.class);

		if (userActivityJobState == null) {
			userActivityJobState = new UserActivityJobState();
			userActivityJobState.setActivityName(activityName);
			userActivityJobState.setLastRun(System.currentTimeMillis());

			mongoTemplate.save(userActivityJobState, UserActivityJobState.COLLECTION_NAME);
		}
		else {
			Update update = new Update();
			update.set(UserActivityJobState.LAST_RUN_FIELD, System.currentTimeMillis());

			mongoTemplate.upsert(query, update, UserActivityJobState.class);

			TreeSet<Long> completedExecutionDays = userActivityJobState.getCompletedExecutionDays();

			long endTime = System.currentTimeMillis();
			long startingTime = TimestampUtils.convertToSeconds(TimestampUtils.toStartOfDay(TimeUtils.
					calculateStartingTime(endTime, numOfLastDaysToCalculate)));

			completedExecutionDays.removeIf(a -> (a < startingTime));

			removeRelevantDocuments(startingTime, relatedDocuments);
		}

		return userActivityJobState;
	}

	public void updateJobState(UserActivityJobState userActivityJobState, Long startOfDay) {
		Query query = new Query();
		query.addCriteria(Criteria.where(UserActivityJobState.ID_FIELD).is(userActivityJobState.getId()));

		userActivityJobState.getCompletedExecutionDays().add(startOfDay);

		Update update = new Update();
		update.set(UserActivityJobState.COMPLETED_EXECUTION_DAYS_FIELD, userActivityJobState.getCompletedExecutionDays());

		mongoTemplate.upsert(query, update, UserActivityJobState.class);
	}

	public void insertUsersActivityToDB(Collection<UserActivityDocument> userActivityToInsert, String collectionName) {
		long insertStartTime = System.nanoTime();
		mongoTemplate.insert(userActivityToInsert, collectionName);
		long elapsedInsertTime = System.nanoTime() - insertStartTime;
		logger.info("Insertion of {} users to Mongo took {} seconds", userActivityToInsert.size(), durationInSecondsWithPrecision(elapsedInsertTime));
	}

	private double durationInSecondsWithPrecision(long updateUsersHistogramInMemoryElapsedTime) {
		return (double) TimeUnit.MILLISECONDS.convert(updateUsersHistogramInMemoryElapsedTime, TimeUnit.NANOSECONDS) / 1000;
	}

	public <T> void saveDocument(String collectionName, T document){
		mongoTemplate.save(document, OrganizationActivityLocationDocument.COLLECTION_NAME);
	}
}
