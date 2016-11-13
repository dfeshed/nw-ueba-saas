package fortscale.aggregation.feature.event.store;

import com.mongodb.BulkWriteResult;
import fortscale.aggregation.feature.event.*;
import fortscale.common.metrics.PersistenceTaskStoreMetrics;
import fortscale.utils.MongoStoreUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.FIndex;
import fortscale.utils.monitoring.stats.StatsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AggregatedFeatureEventsMongoStore implements ScoredEventsCounterReader {
	public static final String COLLECTION_NAME_PREFIX = "scored_";
	public static final String COLLECTION_NAME_SEPARATOR = "__";
	private static final Logger logger = Logger.getLogger(AggregatedFeatureEventsMongoStore.class);

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private StatsService statsService;

    @Value("#{'${fortscale.store.collection.backup.prefix}'.split(',')}")
    private List<String> backupCollectionNamesPrefixes;

	@Value("${streaming.event.field.type.aggr_event}")
	private String eventType;

	private Map<String, String> aggregatedFeatureNameToExistingCollectionNameMap = new HashMap<>();
	private List<String> allAggrFeatureEventCollectionNames;

	public void storeEvent(AggrEvent aggregatedFeatureEvent) {
		String aggregatedFeatureName = aggregatedFeatureEvent.getAggregatedFeatureName();
		String collectionName = createCollectionIfNotExist(aggregatedFeatureName);
		MongoStoreUtils.getCollectionMetrics(statsService, collectionName).writes++;
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
			List<AggrEvent> aggrEvents = collectionToAggrEventListMap.get(collectionName);
			bulkInsertAggrEvents(collectionName, aggrEvents);
		}
	}

	private Query createTimeRangeQuery(Date startTime, Date endTime) {
		Criteria startTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_START_TIME).gte(startTime);
		Criteria endTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_END_TIME).lte(endTime);
		return new Query(startTimeCriteria).addCriteria(endTimeCriteria);
	}

	/**
	 * stores aggrEvents into collection in unordered bulk operation
	 * @param collectionName where to insert
	 * @param aggrEvents what to insert
     */
	private void bulkInsertAggrEvents(String collectionName, List<AggrEvent> aggrEvents) {
		PersistenceTaskStoreMetrics collectionMetrics = MongoStoreUtils.getCollectionMetrics(statsService, collectionName);
		try {
			if(aggrEvents.isEmpty())
			{
				return;
			}
            BulkWriteResult bulkOpResult = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, collectionName)
                    .insert(aggrEvents).execute();
            if (bulkOpResult.isAcknowledged()) {
                int actualInsertedCount = bulkOpResult.getInsertedCount();
                collectionMetrics.bulkWrites++;
                collectionMetrics.bulkWriteDocumentCount += actualInsertedCount;
                collectionMetrics.writes += actualInsertedCount;
                logger.debug("inserted={} documents into collection={} in bulk insert", actualInsertedCount, collectionName);
            } else {
                collectionMetrics.bulkWritesNotAcknowledged++;
                logger.error("bulk insert into collection={} wan't acknowledged", collectionName);
            }
        } catch (BulkOperationException e) {
			// TODO: 10/6/16 DPM client should be aware of this failure
			collectionMetrics.bulkWritesErrors++;
            logger.error("failed to perform bulk insert into collection={}", collectionName, e);
            throw e;
        }
	}

	@SuppressWarnings("unchecked")
	public List<String> findDistinctContextsByTimeRange(
			AggregatedFeatureEventConf aggregatedFeatureEventConf, Date startTime, Date endTime) {

		String aggregatedFeatureName = aggregatedFeatureEventConf.getName();

		String metricsCollectionName = getCollectionName(aggregatedFeatureName);
		MongoStoreUtils.getCollectionMetrics(statsService, metricsCollectionName).reads++;
		Query query = createTimeRangeQuery(startTime, endTime);

        return (getCollectionsNames(aggregatedFeatureName).stream()
                .flatMap(collectionName -> runDistinctContextQuery(collectionName,query).stream())
                .collect(Collectors.toSet())).stream().collect(Collectors.toList());
	}

	private List<String> runDistinctContextQuery(String collectionName, Query query) {
		return mongoTemplate.getCollection(collectionName)
                    .distinct(AggrEvent.EVENT_FIELD_CONTEXT_ID, query.getQueryObject());
	}

	public List<AggrEvent> findAggrEventsByContextIdAndTimeRange(
			AggregatedFeatureEventConf aggregatedFeatureEventConf,
			String contextId, Date startTime, Date endTime) {
		String aggregatedFeatureName = aggregatedFeatureEventConf.getName();
		String metricsCollectionName = getCollectionName(aggregatedFeatureName);
		MongoStoreUtils.getCollectionMetrics(statsService, metricsCollectionName).reads++;
		Query query = createTimeRangeQuery(startTime, endTime)
				.addCriteria(Criteria.where(AggrEvent.EVENT_FIELD_CONTEXT_ID).is(contextId));

        return getCollectionsNames(aggregatedFeatureName).stream()
            .flatMap(collectionName -> mongoTemplate.find(query, AggrEvent.class, collectionName).stream())
            .collect(Collectors.toList());
	}

	private List<String> getCollectionsNames(String aggregatedFeatureName)
	{
		String collectionName = getCollectionName(aggregatedFeatureName);
		List<String> collectionsNames = new ArrayList<>();
		backupCollectionNamesPrefixes.forEach(backupPrefix ->
				collectionsNames.add(String.format("%s%s",backupPrefix,collectionName)));
		collectionsNames.add(collectionName);
		return collectionsNames;
	}

	public long findNumOfAggrEventsByTimeRange(AggregatedFeatureEventConf aggregatedFeatureEventConf,
											   Date startTime,
											   Date endTime) {
		return getCollectionsNames(aggregatedFeatureEventConf.getName()).stream()
				.mapToLong(collectionName -> mongoTemplate.count(createTimeRangeQuery(startTime, endTime), AggrEvent.class, collectionName))
				.sum();
	}

	public AggrEvent findAggrEventWithTopKScore(AggregatedFeatureEventConf aggregatedFeatureEventConf,
												Date startTime,
												Date endTime,
												int k) {
		Query query = createTimeRangeQuery(startTime, endTime)
				.with(new Sort(Sort.Direction.DESC, AggrEvent.EVENT_FIELD_SCORE))
				.limit(k);
		return getCollectionsNames(aggregatedFeatureEventConf.getName()).stream()
				.flatMap(collectionName -> mongoTemplate.find(query, AggrEvent.class, collectionName).stream())
				.sorted(Comparator.comparing(aggrEvent -> aggrEvent.getScore()))
				.skip(k - 1)
				.findFirst()
				.get();
	}

	public Map<Long, List<AggrEvent>> getDateToTopAggrEvents(AggregatedFeatureEventConf aggregatedFeatureEventConf,
															 Date endTime,
															 int numOfDays,
															 int topK) {
		return MongoStoreUtils.getDateToTopScoredEvents(
				statsService,
				mongoTemplate,
				getCollectionName(aggregatedFeatureEventConf),
				AggrEvent.EVENT_FIELD_END_TIME_UNIX,
				AggrEvent.EVENT_FIELD_SCORE,
				endTime,
				numOfDays,
				topK,
				AggrEvent.class
		);
	}

	private String getCollectionName(String aggregatedFeatureName) {
		return StringUtils.join(
				COLLECTION_NAME_PREFIX, COLLECTION_NAME_SEPARATOR,
				eventType, COLLECTION_NAME_SEPARATOR,
				aggregatedFeatureName);
	}

	private String getCollectionName(AggregatedFeatureEventConf aggregatedFeatureEventConf) {
		return getCollectionName(aggregatedFeatureEventConf.getName());
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
		mongoTemplate.indexOps(collectionName).ensureIndex(new FIndex()
				.on(AggrEvent.EVENT_FIELD_CONTEXT_ID, Sort.Direction.ASC)
				.on(AggrEvent.EVENT_FIELD_START_TIME, Sort.Direction.ASC));
	}

	@Override
	public long getTotalNumberOfScoredEvents() {
		if(allAggrFeatureEventCollectionNames ==null) {
			allAggrFeatureEventCollectionNames = new ArrayList<>();
            aggregatedFeatureEventsConfService.getAggrFeatureEventNameList().forEach(aggrFeatureEventName ->
                    allAggrFeatureEventCollectionNames.addAll(getCollectionsNames(aggrFeatureEventName)));
        }

		long totalNumberOfEvents = 0;

		for(String collectionName: allAggrFeatureEventCollectionNames) {
			MongoStoreUtils.getCollectionMetrics(statsService, collectionName).reads++;
			totalNumberOfEvents += mongoTemplate.count(new Query(), collectionName);
		}

		return totalNumberOfEvents;
	}
}
