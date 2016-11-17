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
 * @author Amir Keren
 * Date: 15/11/2015
 */
public class FeatureBucketQueryMongoService implements FeatureBucketQueryService {

	private static final String COLLECTIONS_NAMES_DELIMTER =",";

    @Autowired
    private MongoTemplate mongoTemplate;

	@Value("${fortscale.store.collection.backup.prefix}")
	private String collectionsBackupPrefixListAsString;

    @Override
    public List<FeatureBucket> getFeatureBucketsByContextAndTimeRange(String featureName, String contextType, String ContextName, Long startTime, Long endTime) {

		List<FeatureBucket> result = new ArrayList<>();

		//In case of backups collections - will create the backup prefix list
		List<String> collectionsBackupPrefixList = ConversionUtils.convertStringToList(collectionsBackupPrefixListAsString,COLLECTIONS_NAMES_DELIMTER);

        String collectionName = featureName;

		//Get the data from the origin collection
		result = readFromMongo(collectionName , contextType,  ContextName,  startTime,  endTime);


		//get the data from each of the backup collections and combine it with the origin collection
		final List<FeatureBucket> finalResult = result;
		collectionsBackupPrefixList.forEach(prefix->{
			if(!org.apache.commons.lang.StringUtils.isEmpty(prefix))
				finalResult.addAll(readFromMongo(prefix + collectionName, contextType, ContextName, startTime, endTime));
		});

		return finalResult;

    }

	private List<FeatureBucket> readFromMongo (String collectionName,String contextType, String ContextName, Long startTime, Long endTime)
	{
		try{
			Criteria startTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));
			Criteria endTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(endTime));
			Criteria contextCriteria = createContextCriteria(contextType, ContextName);
			Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria,contextCriteria));
			return mongoTemplate.find(query, FeatureBucket.class, collectionName);
		}
		catch (Exception e) {
			throw new RuntimeException("Could not fetch feature buckets from collection " + collectionName + " due to: " + e);
		}

	}

    private Criteria createContextCriteria(String contextType, String contextName) {
        Map<String, String> contextMap = new HashMap(1);
        contextMap.put(contextType, contextName);
        return Criteria.where(FeatureBucket.CONTEXT_FIELD_NAME_TO_VALUE_MAP_FIELD).in(contextMap);
    }


}
