package fortscale.utils;

import fortscale.common.metrics.PersistenceTaskStoreMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.time.TimestampUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

public class MongoStoreUtils {
	private static final int SECONDS_IN_DAY = 24 * 60 * 60;

	private static Map<String,PersistenceTaskStoreMetrics> collectionMetricsMap;

	public static <T> Map<Long, List<T>> getDateToTopScoredEvents(StatsService statsService,
																  MongoTemplate mongoTemplate,
																  String collectionName,
																  String endTimeFieldName,
																  String scoreName,
																  Date endTime,
																  int numOfDays,
																  int topK,
																  Class<T> scoredEventClass) {
		if (!mongoTemplate.collectionExists(collectionName)) {
			return Collections.emptyMap();
		}
		PersistenceTaskStoreMetrics collectionMetrics = getCollectionMetrics(statsService, collectionName);
		long endTimeSeconds = TimestampUtils.convertToSeconds(endTime);
		Map<Long, List<T>> dateToHighestScoredEvents = new HashMap<>(numOfDays);
		while (numOfDays-- > 0) {
			long startTime = endTimeSeconds - SECONDS_IN_DAY;
			Query query = new Query()
					.addCriteria(Criteria.where(endTimeFieldName)
							.gt(startTime)
							.lte(endTimeSeconds))
					.with(new Sort(Sort.Direction.DESC, scoreName))
					.limit(topK);
			dateToHighestScoredEvents.put(startTime, mongoTemplate.find(query, scoredEventClass, collectionName));
			collectionMetrics.reads++;
			endTimeSeconds -= SECONDS_IN_DAY;
		}
		return dateToHighestScoredEvents;
	}

	/**
	 * CRUD operations are kept at {@link this#collectionMetricsMap}.
	 * before any crud is preformed in this class, this method should be called
	 *
	 * @param collectionName metrics are per collection
	 * @return metrics for collection
	 */
	public static PersistenceTaskStoreMetrics getCollectionMetrics(StatsService statsService, String collectionName)
	{
		if(collectionMetricsMap ==null)
		{
			collectionMetricsMap = new HashMap<>();
		}

		if(!collectionMetricsMap.containsKey(collectionName))
		{
			PersistenceTaskStoreMetrics collectionMetrics =
					new PersistenceTaskStoreMetrics(statsService,collectionName);
			collectionMetricsMap.put(collectionName,collectionMetrics);
		}

		return collectionMetricsMap.get(collectionName);
	}
}
