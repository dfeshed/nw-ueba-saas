package fortscale.entity.event;

import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.utils.mongodb.FIndex;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class EntityEventDataMongoStore implements EntityEventDataStore {
	private static final int EXPIRE_AFTER_DAYS_DEFAULT = 90;

	@Value("${streaming.event.field.type.entity_event}")
	private String eventTypeFieldValue;
	@Value("#{'${fortscale.store.collection.backup.prefix}'.split(',')}")
	private List<String> backupCollectionNamesPrefixes;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;

	@Override
	public EntityEventData getEntityEventData(String entityEventName, String contextId, long startTime, long endTime) {
		String collectionName = getCollectionName(entityEventName);
		Query query = new Query();
		query.addCriteria(where(EntityEventData.CONTEXT_ID_FIELD).is(contextId));
		query.addCriteria(where(EntityEventData.START_TIME_FIELD).is(startTime));
		query.addCriteria(where(EntityEventData.END_TIME_FIELD).is(endTime));
		return mongoTemplate.findOne(query, EntityEventData.class, collectionName);
	}

	private Query getEntityEventDataWithModifiedAtEpochtimeLteQuery(long modifiedAtEpochtime) {
		Query query = new Query();
		Date modifiedAtDate = new Date(TimestampUtils.convertToMilliSeconds(modifiedAtEpochtime));
		query.addCriteria(where(EntityEventData.MODIFIED_AT_DATE_FIELD).lte(modifiedAtDate));
		query.with(new Sort(Direction.ASC, EntityEventData.START_TIME_FIELD));
		return query;
	}

	@Override
	public List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLte(String entityEventName, long modifiedAtEpochtime) {
		String collectionName = getCollectionName(entityEventName);
		Query query = getEntityEventDataWithModifiedAtEpochtimeLteQuery(modifiedAtEpochtime);
		return mongoTemplate.find(query, EntityEventData.class, collectionName);
	}

	@Override
	public List<EntityEventMetaData> getEntityEventDataThatWereNotTransmittedOnlyIncludeIdentifyingData(String entityEventName, PageRequest pageRequest) {
		String collectionName = getCollectionName(entityEventName);
		Query query = new Query();
		query.addCriteria(where(EntityEventData.TRANSMITTED_FIELD).is(false));
		query.fields().include(EntityEventData.ENTITY_EVENT_NAME_FIELD);
		query.fields().include(EntityEventData.CONTEXT_ID_FIELD);
		query.fields().include(EntityEventData.START_TIME_FIELD);
		query.fields().include(EntityEventData.END_TIME_FIELD);
		if(pageRequest != null){
			query.with(pageRequest);
		}
		return mongoTemplate.find(query, EntityEventMetaData.class, collectionName);
	}

	@SuppressWarnings("unchecked")
	public Set<String> findDistinctContextsByTimeRange(
			EntityEventConf entityEventConf, Date startTime, Date endTime) {

		String entityEventConfName = entityEventConf.getName();
		String collectionName = getCollectionName(entityEventConfName);

		long startTimeSeconds = TimestampUtils.convertToSeconds(startTime.getTime());
		long endTimeSeconds = TimestampUtils.convertToSeconds(endTime.getTime());

		Query query = new Query();
		query.addCriteria(where(EntityEventData.START_TIME_FIELD).gte(startTimeSeconds));
		query.addCriteria(where(EntityEventData.END_TIME_FIELD).lte(endTimeSeconds));
		return (Set<String>) mongoTemplate.getCollection(collectionName).distinct(EntityEventData.CONTEXT_ID_FIELD, query.getQueryObject()).stream().collect(Collectors.toSet());
	}

	private String getCollectionName(String entityEventName) {
		return String.format("%s_%s", eventTypeFieldValue, entityEventName);
	}

	private List<String> getCollectionNames(String entityEventName) {
		String collectionName = getCollectionName(entityEventName);
		return Stream.concat(Stream.of(""), backupCollectionNamesPrefixes.stream())
				.map(prefix -> prefix + collectionName)
				.collect(Collectors.toList());
	}

	public List<EntityEventData> findEntityEventsJokerDataByContextIdAndTimeRange(EntityEventConf entityEventConf,
																				  String contextId,
																				  long startTimeSeconds,
																				  long endTimeSeconds) {
		return getCollectionNames(entityEventConf.getName()).stream()
				.flatMap(collectionName -> findEntityEventsJokerDataByContextIdAndTimeRange(
						collectionName, contextId, startTimeSeconds, endTimeSeconds).stream())
				.collect(Collectors.toList());
	}

	private List<EntityEventData> findEntityEventsJokerDataByContextIdAndTimeRange(String collectionName,
																				   String contextId,
																				   long startTimeSeconds,
																				   long endTimeSeconds) {
		Query query = new Query();
		query.addCriteria(where(EntityEventData.CONTEXT_ID_FIELD).is(contextId));
		query.addCriteria(where(EntityEventData.START_TIME_FIELD).gte(startTimeSeconds));
		query.addCriteria(where(EntityEventData.END_TIME_FIELD).lte(endTimeSeconds));
		query.fields().include(EntityEventData.START_TIME_FIELD);
		query.fields().include(EntityEventData.END_TIME_FIELD);

		query.fields().include("includedAggrFeatureEvents.score");
		query.fields().include("includedAggrFeatureEvents.aggregated_feature_type");
		query.fields().include("includedAggrFeatureEvents.aggregated_feature_value");
		query.fields().include("includedAggrFeatureEvents.aggregated_feature_name");
		query.fields().include("includedAggrFeatureEvents.bucket_conf_name");

		query.fields().include("notIncludedAggrFeatureEvents.score");
		query.fields().include("notIncludedAggrFeatureEvents.aggregated_feature_type");
		query.fields().include("notIncludedAggrFeatureEvents.aggregated_feature_value");
		query.fields().include("notIncludedAggrFeatureEvents.aggregated_feature_name");
		query.fields().include("notIncludedAggrFeatureEvents.bucket_conf_name");
		return mongoTemplate.find(query, EntityEventData.class, collectionName);
	}

	@Override
	public List<EntityEventData> getEntityEventDataWithEndTimeInRange(String entityEventName, Date fromTime, Date toTime) {
		String collectionName = getCollectionName(entityEventName);
		long fromTimeSeconds = TimestampUtils.convertToSeconds(fromTime.getTime());
		long toTimeSeconds = TimestampUtils.convertToSeconds(toTime.getTime());

		Query query = new Query();
		query.addCriteria(where(EntityEventData.END_TIME_FIELD).gt(fromTimeSeconds).lte(toTimeSeconds));
		query.with(new Sort(Direction.ASC, EntityEventData.END_TIME_FIELD));
		return mongoTemplate.find(query, EntityEventData.class, collectionName);
	}

	@Override
	public void storeEntityEventData(EntityEventData entityEventData) {
		String entityEventName = entityEventData.getEntityEventName();
		String collectionName = getCollectionName(entityEventName);

		createCollectionIfNotExist(collectionName, entityEventName);

		mongoTemplate.save(entityEventData, collectionName);
	}

	@Override
	public void storeEntityEventDataList(List<EntityEventData> entityEventDataList) {
		Map<String, List<EntityEventData>> collectionNameToEntityEventDataMap = new HashMap<>();
		for(EntityEventData entityEventData: entityEventDataList){
			if(entityEventData.getId() != null){
				storeEntityEventData(entityEventData);
			} else {
				String entityEventName = entityEventData.getEntityEventName();
				String collectionName = getCollectionName(entityEventName);

				List<EntityEventData> collectionEntityEventDatas =  collectionNameToEntityEventDataMap.get(collectionName);
				if (collectionEntityEventDatas == null) {
					collectionEntityEventDatas = new ArrayList<>();
					collectionNameToEntityEventDataMap.put(collectionName, collectionEntityEventDatas);
					createCollectionIfNotExist(collectionName, entityEventName);
				}
				collectionEntityEventDatas.add(entityEventData);
			}
		}

		for(String collectionName:  collectionNameToEntityEventDataMap.keySet()){
			mongoTemplate.insert( collectionNameToEntityEventDataMap.get(collectionName), collectionName);
		}

	}

	private void createCollectionIfNotExist(String collectionName, String entityEventName){
		if (!mongoDbUtilService.collectionExists(collectionName)) {
			mongoDbUtilService.createCollection(collectionName);

			// Context ID + start time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(EntityEventData.CONTEXT_ID_FIELD, Direction.ASC)
					.on(EntityEventData.START_TIME_FIELD, Direction.ASC));

			// Transmitted + end time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(EntityEventData.TRANSMITTED_FIELD, Direction.ASC)
					.on(EntityEventData.END_TIME_FIELD, Direction.ASC));

			// Start time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(EntityEventData.START_TIME_FIELD, Direction.ASC));

			// End time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(EntityEventData.END_TIME_FIELD, Direction.ASC));

			// Modified at date (TTL)
			int daysToRetainDocument = EXPIRE_AFTER_DAYS_DEFAULT;
			EntityEventConf entityEventConf = entityEventConfService.getEntityEventConf(entityEventName);
			if (entityEventConf != null) {
				Integer daysToRetainDocumentInConf = entityEventConf.getDaysToRetainDocument();
				if (daysToRetainDocumentInConf != null) {
					daysToRetainDocument = daysToRetainDocumentInConf;
				}
			}

			mongoTemplate.indexOps(collectionName).ensureIndex(new FIndex()
					.expire(daysToRetainDocument, TimeUnit.DAYS)
					.named(EntityEventData.MODIFIED_AT_DATE_FIELD)
					.on(EntityEventData.MODIFIED_AT_DATE_FIELD, Direction.ASC));
		}
	}
}
