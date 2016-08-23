package fortscale.utils;

import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

public class MongoStoreUtils {
	private static final int SECONDS_IN_DAY = 24 * 60 * 60;

	@Autowired
	private static MongoTemplate mongoTemplate;

	public static <T> Map<Long, List<T>> getDateToTopScoredEvents(String collectionName,
																  String endTimeFieldName,
																  String scoreName,
																  Date endTime,
																  int numOfDays,
																  int topK,
																  Class<T> scoredEventClass) {
		if (!mongoTemplate.collectionExists(collectionName)) {
			return Collections.emptyMap();
		}
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
			endTimeSeconds -= SECONDS_IN_DAY;
		}
		return dateToHighestScoredEvents;
	}
}
