package fortscale.aggregation.feature.event;

import fortscale.utils.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Service implementation of basic query functionality for aggregated events, based on mongo persistence
 *
 * @author gils
 * Date: 10/09/2015
 */
public class AggregatedEventQueryMongoService implements AggregatedEventQueryService {

	private static final String SCORED_AGGR_EVENT_COLLECTION_PREFIX = "scored___aggr_event__";
	private static final String COLLECTIONS_NAMES_DELIMITER =",";
	private static final String CONTEXT_ID_DELIMITER = "#";

	@Value("${fortscale.store.collection.backup.prefix}")
	private String collectionsBackupPrefixListAsString;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<AggrEvent> getAggregatedEventsByContextIdAndTimeRange(String featureName, String contextType, String contextName, Long startTime, Long endTime) {

		List<AggrEvent> result;

		//In case of backups collections - will create the backup prefix list
		List<String> collectionsBackupPrefixList = ConversionUtils.convertStringToList(collectionsBackupPrefixListAsString, COLLECTIONS_NAMES_DELIMITER);


		String collectionName = SCORED_AGGR_EVENT_COLLECTION_PREFIX + featureName;

		//Get the data from the origin collection
		result = readFromMongo(collectionName , contextType, contextName,  startTime,  endTime);

		//get the data from each of the backup collections and combine it with the origin collection
		final List<AggrEvent> finalResult = result;
		collectionsBackupPrefixList.forEach(prefix->{
			if(!org.apache.commons.lang.StringUtils.isEmpty(prefix))
				finalResult.addAll(readFromMongo(prefix + collectionName, contextType, contextName, startTime, endTime));
		});

		return finalResult;

	}

	private List<AggrEvent> readFromMongo(String collectionName,String contextType, String contextName, Long startTimeInMillis, Long endTimeInMillis) {

		Date startTimeDate = new Date(startTimeInMillis);
		Criteria startTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_START_TIME).gte(startTimeDate);

		Date endTimeDate = new Date(endTimeInMillis);
		Criteria endTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_START_TIME).lte(endTimeDate);

		final HashMap<String, String> context = new HashMap<>();
		context.put(contextType, contextName);
		String contextId = AggrFeatureEventBuilderService.getAggregatedFeatureContextId(context);
		Criteria contextCriteria = Criteria.where(AggrEvent.EVENT_FIELD_CONTEXT_ID).is(contextId);

		Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria, contextCriteria));
		try {
			return mongoTemplate.find(query, AggrEvent.class, collectionName);
		}
		catch (Exception e) {
			throw new RuntimeException("Could not fetch aggregated events from collection " + collectionName + " due to: " + e);
		}

	}

}
