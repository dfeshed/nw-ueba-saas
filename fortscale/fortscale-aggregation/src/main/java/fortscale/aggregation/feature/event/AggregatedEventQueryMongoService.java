package fortscale.aggregation.feature.event;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.utils.ConversionUtils;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation of basic query functionality for aggregated events, based on mongo persistence
 *
 * @author gils
 * Date: 10/09/2015
 */
public class AggregatedEventQueryMongoService implements AggregatedEventQueryService {

    private static final String SCORED_AGGR_EVENT_COLLECTION_PREFIX = "scored___aggr_event__";
	private static final String COLLECTIONS_NAMES_DELIMTER =",";

	@Value("${fortscale.store.collection.backup.prefix}")
	private String collectionsBackupPrefixListAsString;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<AggrEvent> getAggregatedEventsByContextAndTimeRange(String featureName, String contextType, String ContextName, Long startTime, Long endTime) {

		List<AggrEvent> result = new ArrayList<>();

		//In case of backups collections - will create the backup prefix list
		List<String> collectionsBackupPrefixList = ConversionUtils.convertStringToList(collectionsBackupPrefixListAsString, COLLECTIONS_NAMES_DELIMTER);


        String collectionName = SCORED_AGGR_EVENT_COLLECTION_PREFIX + featureName;

		//Get the data from the origin collection
		result = readFromMongo(collectionName , contextType,  ContextName,  startTime,  endTime);

		//get the data from each of the backup collections and combine it with the origin collection
		final List<AggrEvent> finalResult = result;
		collectionsBackupPrefixList.forEach(prefix->{
			if(!org.apache.commons.lang.StringUtils.isEmpty(prefix))
				finalResult.addAll(readFromMongo(prefix + collectionName, contextType, ContextName, startTime, endTime));
		});

		return result;

    }

	private List<AggrEvent> readFromMongo (String collectionName,String contextType, String ContextName, Long startTime, Long endTime)
	{
		try{
			Criteria startTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));
			Criteria endTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(endTime));
			Criteria contextCriteria = createContextCriteria(contextType, ContextName);
			Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria,contextCriteria));
			return mongoTemplate.find(query, AggrEvent.class, collectionName);
		}
		catch (Exception e) {
			throw new RuntimeException("Could not fetch aggregated events from collection " + collectionName + " due to: " + e);
		}

	}

    private Criteria createContextCriteria(String contextType, String contextName) {
        Map<String, String> contextMap = new HashMap<>(1);
        contextMap.put(contextType, contextName);

        return Criteria.where(AggrEvent.EVENT_FIELD_CONTEXT).in(contextMap); // TODO check for multiple context, might not work
    }
}
