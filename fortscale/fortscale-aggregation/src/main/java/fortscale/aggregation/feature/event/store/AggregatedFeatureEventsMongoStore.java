package fortscale.aggregation.feature.event.store;

import fortscale.aggregation.feature.event.*;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationService;
import fortscale.utils.mongodb.FIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AggregatedFeatureEventsMongoStore implements ScoredEventsCounterReader {


	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

	@Autowired
	private AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService;

    @Value("#{'${fortscale.store.collection.backup.prefix}'.split(',')}")
    private List<String> backupCollectionNamesPrefixes;


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
			collectionName = aggregatedFeatureNameTranslationService.toCollectionName(aggregatedFeatureName);
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

		Criteria startTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_START_TIME).gte(startTime);
		Criteria endTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_END_TIME).lte(endTime);
		Query query = new Query(startTimeCriteria).addCriteria(endTimeCriteria);


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

		Criteria contextIdCriteria = Criteria.where(AggrEvent.EVENT_FIELD_CONTEXT_ID).is(contextId);
		Criteria startTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_START_TIME).gte(startTime);
		Criteria endTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_END_TIME).lte(endTime);
		Query query = new Query(contextIdCriteria.andOperator(startTimeCriteria, endTimeCriteria));

        return getRunQueryOnAllFeatureNameCollections(aggregatedFeatureName, query);
	}

	private List<String> getCollectionsNames(String aggregatedFeatureName)
	{
		String collectionName = aggregatedFeatureNameTranslationService.toCollectionName(aggregatedFeatureName);
		List<String> collectionsNames = new ArrayList<>();
		backupCollectionNamesPrefixes.forEach(backupPrefix ->
				collectionsNames.add(String.format("%s%s",backupPrefix,collectionName)));
		collectionsNames.add(collectionName);
		return collectionsNames;
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
			totalNumberOfEvents += mongoTemplate.count(new Query(), collectionName);
		}

		return totalNumberOfEvents;
	}

	/**
	 *
	 * @param from greater than/equal of that date
	 * @param to before or/equal to that date
	 * @param aggregatedFeatureName feature to run on
     * @return list of {@link AggrEvent} between thos dates
     */
	public List<AggrEvent> findAggrEventsByTimeRange(Instant from, Instant to, String aggregatedFeatureName) {

		Criteria startTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_START_TIME).gte(from);
		Criteria endTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_END_TIME).lte(to);
		Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria));

		return getRunQueryOnAllFeatureNameCollections(aggregatedFeatureName, query);
	}

	/**
	 * one {@param aggregatedFeatureName} my contain several splitted collections in mongoDb (in case of large scale),
	 * this method executes given {@param query} on all collectionNames
     * @return list of {@link AggrEvent} answering the query
     */
	public List<AggrEvent> getRunQueryOnAllFeatureNameCollections(String aggregatedFeatureName, Query query) {
		return getCollectionsNames(aggregatedFeatureName).stream()
				.flatMap(collectionName -> mongoTemplate.find(query, AggrEvent.class, collectionName).stream())
				.collect(Collectors.toList());
	}
}
