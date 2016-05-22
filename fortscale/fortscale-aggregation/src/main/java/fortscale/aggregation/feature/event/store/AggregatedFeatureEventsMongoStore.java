package fortscale.aggregation.feature.event.store;

import fortscale.aggregation.feature.event.*;
import fortscale.utils.mongodb.FIndex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AggregatedFeatureEventsMongoStore implements ScoredEventsCounterReader {
	private static final String COLLECTION_NAME_PREFIX = "scored_";
	private static final String COLLECTION_NAME_SEPARATOR = "__";

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

	@Value("${streaming.event.field.type.aggr_event}")
	private String eventType;

	private Map<String, String> aggregatedFeatureNameToExistingCollectionNameMap = new HashMap<>();
	private List<String> allAggrFeatureEventCollectionNames;

	public void storeEvent(AggrEvent aggregatedFeatureEvent) {
		String aggregatedFeatureName = aggregatedFeatureEvent.getAggregatedFeatureName();
		String collectionName = createCollectionIfNotExist(aggregatedFeatureName);
		// Save aggregated feature event in Mongo collection
		mongoTemplate.save(aggregatedFeatureEvent, collectionName);
	}

	private String createCollectionIfNotExist(String aggregatedFeatureName){
		String collectionName = aggregatedFeatureNameToExistingCollectionNameMap.get(aggregatedFeatureName);
		if(collectionName == null){
			collectionName = getCollectionName(aggregatedFeatureName);
			if(!mongoTemplate.collectionExists(collectionName)){
				createCollection(collectionName, getRetentionInSeconds(aggregatedFeatureName));
			}
			aggregatedFeatureNameToExistingCollectionNameMap.put(aggregatedFeatureName, collectionName);
		}
		return collectionName;
	}

	public void storeEvent(List<AggrEvent> aggregatedFeatureEventList) {
		Map<String,List<AggrEvent>> collectionToAggrEventListMap = new HashMap<>();
		for(AggrEvent aggregatedFeatureEvent: aggregatedFeatureEventList) {
			String aggregatedFeatureName = aggregatedFeatureEvent.getAggregatedFeatureName();
			String collectionName = createCollectionIfNotExist(aggregatedFeatureName);

			List<AggrEvent> collectionAggrEvents = collectionToAggrEventListMap.get(collectionName);
			if(collectionAggrEvents == null){
				collectionAggrEvents = new ArrayList<>();
				collectionToAggrEventListMap.put(collectionName,collectionAggrEvents);
			}
			collectionAggrEvents.add(aggregatedFeatureEvent);
		}

		for(String collectionName: collectionToAggrEventListMap.keySet()){
			mongoTemplate.insert(collectionToAggrEventListMap.get(collectionName), collectionName);
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> findDistinctContextsByTimeRange(
			AggregatedFeatureEventConf aggregatedFeatureEventConf, Date startTime, Date endTime) {

		String aggregatedFeatureName = aggregatedFeatureEventConf.getName();
		String collectionName = getCollectionName(aggregatedFeatureName);

		if (mongoTemplate.collectionExists(collectionName)) {
			Criteria startTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_START_TIME).gte(startTime);
			Criteria endTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_END_TIME).lte(endTime);
			Query query = new Query(startTimeCriteria).addCriteria(endTimeCriteria);
			return mongoTemplate.getCollection(collectionName)
					.distinct(AggrEvent.EVENT_FIELD_CONTEXT_ID, query.getQueryObject());
		} else {
			return Collections.emptyList();
		}
	}

	public List<AggrEvent> findAggrEventsByContextIdAndTimeRange(
			AggregatedFeatureEventConf aggregatedFeatureEventConf,
			String contextId, Date startTime, Date endTime) {

		String aggregatedFeatureName = aggregatedFeatureEventConf.getName();
		String collectionName = getCollectionName(aggregatedFeatureName);

		if (mongoTemplate.collectionExists(collectionName)) {
			Criteria contextIdCriteria = Criteria.where(AggrEvent.EVENT_FIELD_CONTEXT_ID).is(contextId);
			Criteria startTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_START_TIME).gte(startTime);
			Criteria endTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_END_TIME).lte(endTime);
			Query query = new Query(contextIdCriteria.andOperator(startTimeCriteria, endTimeCriteria));
			return mongoTemplate.find(query, AggrEvent.class, collectionName);
		} else {
			return Collections.emptyList();
		}
	}

	private String getCollectionName(String aggregatedFeatureName) {
		return StringUtils.join(
				COLLECTION_NAME_PREFIX, COLLECTION_NAME_SEPARATOR,
				eventType, COLLECTION_NAME_SEPARATOR,
				aggregatedFeatureName);
	}

	private long getRetentionInSeconds(String aggregatedFeatureName) {
		// Get retention strategy name
		AggregatedFeatureEventConf aggregatedFeatureEventConf =
				aggregatedFeatureEventsConfService
				.getAggregatedFeatureEventConf(aggregatedFeatureName);
		String retentionStrategyName = aggregatedFeatureEventConf.getRetentionStrategyName();

		// Assert retention strategy name
		if (retentionStrategyName == null) {
			String errorMsg = String.format(
					"Aggregated feature conf doesn't have retention strategy. Feature name: %s",
					aggregatedFeatureName);
			throw new IllegalArgumentException(errorMsg);
		}

		// Get retention strategy
		AggrFeatureRetentionStrategy retentionStrategy =
				aggregatedFeatureEventsConfService
				.getAggrFeatureRetnetionStrategy(retentionStrategyName);

		// Assert retention strategy
		if (retentionStrategy == null) {
			String errorMsg = String.format(
					"No aggregated feature retention strategy with the name [%s] exists in the %s",
					retentionStrategyName, AggregatedFeatureEventsConfService.class.getSimpleName());
			throw new IllegalArgumentException(errorMsg);
		}

		// Return retention
		return retentionStrategy.getRetentionInSeconds();
	}

	private void createCollection(String collectionName, long retentionInSeconds) {
		// Create collection
		mongoTemplate.createCollection(collectionName);

		// Ensure indexes
		mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
				.on(AggrEvent.EVENT_FIELD_START_TIME_UNIX, Sort.Direction.DESC));
		mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
				.on(AggrEvent.EVENT_FIELD_CONTEXT, Sort.Direction.DESC));
		mongoTemplate.indexOps(collectionName).ensureIndex(new FIndex()
				.expire(retentionInSeconds, TimeUnit.SECONDS)
				.named(AggrEvent.EVENT_FIELD_CREATION_DATE_TIME)
				.on(AggrEvent.EVENT_FIELD_CREATION_DATE_TIME, Sort.Direction.DESC));
	}

	@Override
	public long getTotalNumberOfScoredEvents() {
		if(allAggrFeatureEventCollectionNames ==null) {
			allAggrFeatureEventCollectionNames = new ArrayList<>();
			aggregatedFeatureEventsConfService.getAggrFeatureEventNameList().forEach(aggrFeatureEventName ->
					allAggrFeatureEventCollectionNames.add(getCollectionName(aggrFeatureEventName)));
		}

		long totalNumberOfEvents = 0;

		for(String collectionName: allAggrFeatureEventCollectionNames) {
			totalNumberOfEvents += mongoTemplate.count(new Query(), collectionName);
		}

		return totalNumberOfEvents;
	}

}
