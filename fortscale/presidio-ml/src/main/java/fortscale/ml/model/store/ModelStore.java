package fortscale.ml.model.store;

import com.mongodb.DBObject;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.TtlService;
import fortscale.utils.ttl.TtlServiceAware;
import javafx.util.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.StreamUtils;
import org.springframework.util.CollectionUtils;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class ModelStore implements TtlServiceAware {
	private static final Logger logger = Logger.getLogger(ModelStore.class);
	private static final String COLLECTION_NAME_PREFIX = "model_";

	private MongoTemplate mongoTemplate;
	private TtlService ttlService;
	private Duration ttlOldestAllowedModel;

	public ModelStore(MongoTemplate mongoTemplate, Duration ttlOldestAllowedModel) {
		this.mongoTemplate = mongoTemplate;
		this.ttlOldestAllowedModel = ttlOldestAllowedModel;
	}

	/**
	 * @param modelConf the {@link ModelConf}
	 * @param sessionId the session ID
	 * @return the latest end time across all models created from the given model conf with the given session ID
	 */
	public Instant getLatestEndTime(ModelConf modelConf, String sessionId) {
		Query query = new Query()
				.addCriteria(Criteria.where(ModelDAO.SESSION_ID_FIELD).is(sessionId))
				.with(new Sort(Direction.DESC, ModelDAO.END_TIME_FIELD));
		ModelDAO modelDao = mongoTemplate.findOne(query, ModelDAO.class, getCollectionName(modelConf));
		return modelDao == null ? null : modelDao.getEndTime();
	}

	public void save(ModelConf modelConf, String sessionId, String contextId, Model model, TimeRange timeRange) {
		ModelDAO modelDao = new ModelDAO(sessionId, contextId, model, timeRange.getStart(), timeRange.getEnd());
		save(modelConf, modelDao);
	}

	public void save(ModelConf modelConf, ModelDAO modelDao) {
		String collectionName = getCollectionName(modelConf);
		mongoTemplate.insert(modelDao, collectionName);
		ttlService.save(getStoreName(), collectionName);
	}

	public List<ModelDAO> getAllContextsModelDaosWithLatestEndTimeLte(ModelConf modelConf, Instant eventEpochtime) {
		String modelDaosGroupName = "modelDaos";
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(new Criteria(ModelDAO.END_TIME_FIELD).lte(Date.from(eventEpochtime))),
				Aggregation.group(ModelDAO.CONTEXT_ID_FIELD).push(Aggregation.ROOT).as(modelDaosGroupName));
		String collectionName = getCollectionName(modelConf);
		AggregationResults<DBObject> results = mongoTemplate.aggregate(aggregation, collectionName, DBObject.class);
		return StreamUtils.createStreamFromIterator(results.iterator())
				.map(modelDaoDbObjects -> {
					ModelDAO[] modelDaos = mongoTemplate.getConverter().read(ModelDAO[].class, (DBObject)modelDaoDbObjects.get(modelDaosGroupName));
					return Arrays.stream(modelDaos).max(Comparator.comparing(ModelDAO::getEndTime)).get();
				})
				.collect(Collectors.toList());
	}

	public ModelDAO getLatestBeforeEventTimeAfterOldestAllowedModelDao(
			ModelConf modelConf, String contextId, Instant eventTime, Instant oldestAllowedModelTime) {

		String collectionName = getCollectionName(modelConf);
		Query query = new Query()
				.addCriteria(Criteria.where(ModelDAO.CONTEXT_ID_FIELD).is(contextId))
				.addCriteria(Criteria.where(ModelDAO.END_TIME_FIELD).lte(eventTime).gte(oldestAllowedModelTime))
				.with(new Sort(Direction.DESC, ModelDAO.END_TIME_FIELD))
				.limit(1);
		logger.debug("Fetching latest model DAO for contextId = {} eventTime = {} collection = {}.", contextId, eventTime, collectionName);
		List<ModelDAO> queryResult = mongoTemplate.find(query, ModelDAO.class, collectionName);

		if (CollectionUtils.isEmpty(queryResult)) {
			return null;
		} else {
			return queryResult.stream().findFirst().get();
		}
	}

	public static String getCollectionName(ModelConf modelConf) {
		return COLLECTION_NAME_PREFIX + modelConf.getName();
	}


	@Override
	public void setTtlService(TtlService ttlService) {
		this.ttlService = ttlService;
	}

	@Override
	public void remove(String collectionName, Instant until) {

		//remove models that older than ttlOldestAllowedModel
		removeOldestModels(collectionName, until);

		//remove models of contextIds, where contextId has at least one model that greater than until instant.
		removeContextIdOldModels(collectionName, until);
	}

	/**
	 * Remove models of contextIds, where contextId has at least one model that greater than until instant.
	 * @param collectionName collectionName
	 * @param until until instant
	 */
	private void removeContextIdOldModels(String collectionName, Instant until) {
		Aggregation agg = newAggregation(
				match(where(ModelDAO.END_TIME_FIELD).gte(Date.from(until))),
				Aggregation.group(ModelDAO.CONTEXT_ID_FIELD),
				Aggregation.project(ModelDAO.CONTEXT_ID_FIELD).and("_id").as(ModelDAO.CONTEXT_ID_FIELD).andExclude("_id")
		);

		AggregationResults<DBObject> aggrResult = mongoTemplate.aggregate(agg, collectionName, DBObject.class);
		List<DBObject> results = aggrResult.getMappedResults();

		List<String> contextIds = new ArrayList<>();
		for (DBObject result : results) {
			contextIds.add((String) result.get(ModelDAO.CONTEXT_ID_FIELD));
		}

		Criteria contextCriteria = where(ModelDAO.CONTEXT_ID_FIELD).in(contextIds);
		Criteria dateCriteria = where(ModelDAO.END_TIME_FIELD).lt(Date.from(until));

		Query records = new Query(contextCriteria).addCriteria(dateCriteria);

		mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, collectionName).remove(records).execute();
	}

	/**
	 * Remove models that older than ttlOldestAllowedModel
	 *
	 * @param collectionName collectionName
	 * @param until until instant
	 */
	private void removeOldestModels(String collectionName, Instant until){
		Query OldestModelQuery = new Query()
				.addCriteria(where(ModelDAO.END_TIME_FIELD).lt(until.minus(ttlOldestAllowedModel)));
		mongoTemplate.remove(OldestModelQuery, collectionName);
	}

}
