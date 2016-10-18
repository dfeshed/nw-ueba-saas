package fortscale.entity.event;

import com.mongodb.BulkWriteResult;
import fortscale.aggregation.feature.event.ScoredEventsCounterReader;
import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.common.metrics.PersistenceTaskStoreMetrics;
import fortscale.domain.core.EntityEvent;
import fortscale.entity.event.translator.EntityEventTranslationService;
import fortscale.utils.logging.Logger;
import fortscale.entity.event.translator.EntityEventTranslationService;
import fortscale.utils.MongoStoreUtils;
import fortscale.utils.mongodb.FIndex;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EntityEventMongoStore  implements ScoredEventsCounterReader {

	private static final int SECONDS_IN_DAY = 24 * 60 * 60;
	private static final Logger logger = Logger.getLogger(EntityEventMongoStore.class);
	private Map<String,PersistenceTaskStoreMetrics> collectionMetricsMap;

	@Autowired
	private StatsService statsService;
	@Value("${streaming.event.field.type.entity_event}")
	private String eventTypeFieldValue;


	@Value("#{'${fortscale.store.collection.backup.prefix}'.split(',')}")
	private List<String> backupCollectionNamesPrefixes;
	@Value("${fortscale.scored.entity.event.store.page.size}")
	private int storePageSize;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;
	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private EntityEventTranslationService translationService;

	private Map<String, List<EntityEvent>> collectionToEntityEventListMap = new HashMap<>();
	private List<String> allScoredEntityEventCollectionNames;

	public void save(EntityEvent entityEvent) {
		String collectionName = ensureCollectionExists(entityEvent);
		PersistenceTaskStoreMetrics collectionMetrics = MongoStoreUtils.getCollectionMetrics(statsService, collectionName);
		if (storePageSize > 1) {
			List<EntityEvent> entityEventList = collectionToEntityEventListMap.get(collectionName);
			if (entityEventList == null) {
				entityEventList = new ArrayList<>();
				collectionToEntityEventListMap.put(collectionName, entityEventList);
			}
			entityEventList.add(entityEvent);
			if (entityEventList.size() >= storePageSize) {
				bulkInsertEntityEvents(collectionName,entityEventList);
				collectionToEntityEventListMap.remove(collectionName);
			}
		} else {
			collectionMetrics.writes++;
			mongoTemplate.save(entityEvent, collectionName);
		}
	}

	public void flush() {
		if (collectionToEntityEventListMap.isEmpty()) {
			return;
		}
		for(String collectionName: collectionToEntityEventListMap.keySet()) {

			List<EntityEvent> entityEvents = collectionToEntityEventListMap.get(collectionName);
			bulkInsertEntityEvents(collectionName, entityEvents);
		}
		collectionToEntityEventListMap = new HashMap<>();
	}

	/**
	 * insert bulk of entity events
	 * @param collectionName where to insert
	 * @param entityEvents what to insert
     */
	private void bulkInsertEntityEvents(String collectionName, List<EntityEvent> entityEvents) {
		PersistenceTaskStoreMetrics collectionMetrics = MongoStoreUtils.getCollectionMetrics(statsService, collectionName);
		try {
			if (entityEvents.isEmpty())
			{
				return;
			}
            BulkWriteResult bulkOpResult = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, collectionName)
                    .insert(entityEvents).execute();
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
        }
        catch (BulkOperationException e) {
			// TODO: 10/6/16 DPM client should be aware of this failure
			collectionMetrics.bulkWritesErrors++;
            logger.error("failed to perform bulk insert into collection={}", collectionName, e);
            throw e;
        }
	}

	private String getCollectionName(String entityEventType) {
		return translationService.toCollectionName(entityEventType);
	}

	private String getCollectionName(EntityEvent entityEvent) {
		return getCollectionName(entityEvent.getEntity_event_type());
	}

	public Map<Long, List<EntityEvent>> getDateToTopEntityEvents(String entityEventType, Date endTime, int numOfDays, int topK) {
		return MongoStoreUtils.getDateToTopScoredEvents(
				statsService,
				mongoTemplate,
				getCollectionName(entityEventType),
				EntityEvent.ENTITY_EVENT_END_TIME_UNIX_FIELD_NAME,
				EntityEvent.ENTITY_EVENT_UNREDUCED_SCORE_FIELD_NAME,
				endTime,
				numOfDays,
				topK,
				EntityEvent.class
		);
	}

	private String ensureCollectionExists(EntityEvent entityEvent) {
		String collectionName = getCollectionName(entityEvent);
		if (!mongoDbUtilService.collectionExists(collectionName)) {
			EntityEventConf entityEventConf = entityEventConfService.getEntityEventConf(entityEvent.getEntity_event_type());
			Integer retentionTimeInDays = entityEventConf.getDaysToRetainDocument();

			mongoDbUtilService.createCollection(collectionName);
			mongoTemplate.indexOps(collectionName)
					.ensureIndex(new FIndex().expire(retentionTimeInDays, TimeUnit.DAYS)
							.named(EntityEvent.ENTITY_EVENT_CREATION_TIME_FIELD_NAME)
							.on(EntityEvent.ENTITY_EVENT_CREATION_TIME_FIELD_NAME, Sort.Direction.DESC));
			mongoTemplate.indexOps(collectionName)
					.ensureIndex(new Index().named(EntityEvent.ENTITY_EVENT_END_TIME_UNIX_FIELD_NAME)
							.on(EntityEvent.ENTITY_EVENT_END_TIME_UNIX_FIELD_NAME, Sort.Direction.DESC));
			mongoTemplate.indexOps(collectionName)
					.ensureIndex(new Index().named(EntityEvent.ENTITY_EVENT_SCORE_FIELD_NAME)
							.on(EntityEvent.ENTITY_EVENT_SCORE_FIELD_NAME, Sort.Direction.DESC));
			mongoTemplate.indexOps(collectionName)
					.ensureIndex(new Index().named(EntityEvent.ENTITY_EVENT_UNREDUCED_SCORE_FIELD_NAME)
							.on(EntityEvent.ENTITY_EVENT_UNREDUCED_SCORE_FIELD_NAME, Sort.Direction.DESC));
		}
		return collectionName;
	}


	@Override
	public long getTotalNumberOfScoredEvents() {
		if(allScoredEntityEventCollectionNames ==null) {
			allScoredEntityEventCollectionNames = new ArrayList<>();
			entityEventConfService.getEntityEventNames().forEach(entityEventName ->
					allScoredEntityEventCollectionNames.add(getCollectionName(entityEventName)));
		}

		long totalNumberOfEvents = 0;

		for(String collectionName: allScoredEntityEventCollectionNames) {
			PersistenceTaskStoreMetrics collectionMetrics = MongoStoreUtils.getCollectionMetrics(statsService, collectionName);
			totalNumberOfEvents += mongoTemplate.count(new Query(), collectionName);
			collectionMetrics.reads++;
		}

		return totalNumberOfEvents;
	}

	/**
	 * CRUD operations are kept at {@link this#collectionMetricsMap}.
	 * before any crud is preformed in this class, this method should be called
	 *
	 * @param collectionName metrics are per collection
	 * @return metrics for collection
	 */
	private PersistenceTaskStoreMetrics getCollectionMetrics(String collectionName) {
		if (collectionMetricsMap == null) {
			collectionMetricsMap = new HashMap<>();
		}
	}
	public List<EntityEvent> findEntityEventsByStartTimeRange(Instant from, Instant to, String featureName) {

		if (!collectionMetricsMap.containsKey(collectionName)) {
			PersistenceTaskStoreMetrics collectionMetrics =
					new PersistenceTaskStoreMetrics(statsService, collectionName);
			collectionMetricsMap.put(collectionName, collectionMetrics);
		}
		Criteria startTimeCriteria = Criteria.where(EntityEvent.ENTITY_EVENT_START_TIME_UNIX_FIELD_NAME).gte(from.getEpochSecond()).lt(to);
		Query query = new Query(startTimeCriteria);

		return collectionMetricsMap.get(collectionName);
	}

	public List<EntityEvent> findEntityEventsByTimeRange(Instant fromCursor, Instant toCursor, String featureName) {
		return findEntityEvents(featureName, query);
	}

	/**
	 * one {@param featureName} my contain several splitted collections in mongoDb (in case of large scale),
	 * this method executes given {@param query} on all collectionNames
	 * @return list of {@link EntityEvent} answering the query
	 */
	public List<EntityEvent> findEntityEvents(String featureName, Query query) {
		return getCollectionNames(featureName).stream()
				.flatMap(collectionName -> mongoTemplate.find(query, EntityEvent.class, collectionName).stream())
				.collect(Collectors.toList());
	}

	public Instant getLastEntityEventStartTime(String featureName) {
		Query query = new Query();
		Sort sort = new Sort(Sort.Direction.DESC,
				EntityEvent.ENTITY_EVENT_START_TIME_UNIX_FIELD_NAME);
		query.with(sort);
		query.limit(1);
		List<EntityEvent> queryResult = findEntityEvents(featureName, query);

		if(!queryResult.isEmpty())
		{
			long maxStartDate = queryResult.stream().max(Comparator.comparingLong(EntityEvent::getStart_time_unix)).get().getStart_time_unix();
			return Instant.ofEpochSecond(maxStartDate);
		}
		return null;

	}
}
