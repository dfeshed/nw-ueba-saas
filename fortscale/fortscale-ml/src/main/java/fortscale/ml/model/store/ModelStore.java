package fortscale.ml.model.store;

import com.mongodb.DBObject;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.StreamUtils;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

public class ModelStore {
	private static final String COLLECTION_NAME_PREFIX = "model_";
	private static final String ID_FIELD = "_id";
    private static final Logger logger = Logger.getLogger(ModelStore.class);

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;
	@Autowired
	private StatsService statsService;

	private ModelStoreMetrics metrics;

	public void save(ModelConf modelConf, String sessionId, String contextId, Model model, Date startTime, Date endTime) {
		getMetrics().saveModel++;
		String collectionName = getCollectionName(modelConf);
		ModelDAO newModelDao = new ModelDAO(sessionId, contextId, model, startTime.toInstant(), endTime.toInstant());
		mongoTemplate.insert(newModelDao, collectionName);
	}

	public List<ModelDAO> getModelDaos(ModelConf modelConf, String contextId) {
		getMetrics().getModelDaos++;
		String collectionName = getCollectionName(modelConf);

		Query query = new Query();
		query.addCriteria(Criteria.where(ModelDAO.CONTEXT_ID_FIELD).is(contextId));
		return mongoTemplate.find(query, ModelDAO.class, collectionName);

	}

	public List<ModelDAO> getAllContextsModelDaosWithLatestEndTimeLte(ModelConf modelConf, long eventEpochtime) {
		getMetrics().getModelDaosWithNoContext++;
		String modelDaosGroupName = "modelDaos";
		Aggregation agg = newAggregation(
				Aggregation.match(new Criteria(ModelDAO.END_TIME_FIELD).lte(new Date(eventEpochtime * 1000))),
				Aggregation.group(ModelDAO.CONTEXT_ID_FIELD).push(Aggregation.ROOT).as(modelDaosGroupName)
		);
		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, getCollectionName(modelConf), DBObject.class);
		return StreamUtils.createStreamFromIterator(results.iterator())
				.map(modelDaoDbObjects -> {
					ModelDAO[] modelDaos = mongoTemplate.getConverter().read(ModelDAO[].class, (DBObject) modelDaoDbObjects.get(modelDaosGroupName));
					return Arrays.stream(modelDaos).max(Comparator.comparing(ModelDAO::getEndTime)).get();
				})
				.collect(Collectors.toList());
	}

	public void removeModels(Collection<ModelConf> modelConfs, String sessionId) {
		getMetrics().removeModels++;

		modelConfs.forEach(modelConf -> {
			String collectionName = getCollectionName(modelConf);

			if (mongoTemplate.collectionExists(collectionName)) {
				Query query = new Query();
				query.addCriteria(Criteria.where(ModelDAO.SESSION_ID_FIELD).is(sessionId));
				mongoTemplate.remove(query, ModelDAO.class, collectionName);
			}
		});
	}

	public String getCollectionName(ModelConf modelConf) {
		getMetrics().getCollectionName++;
		return String.format("%s%s", COLLECTION_NAME_PREFIX, modelConf.getName());
	}


	public ModelStoreMetrics getMetrics()
	{
		if (metrics==null)
		{
			metrics = new ModelStoreMetrics(statsService);
		}
		return metrics;
	}

    public ModelDAO getLatestBeforeEventTimeAfterOldestAllowedModelDao(ModelConf modelConf, String contextId, Instant eventTime, Instant oldestAllowedModelTime) {
        String collectionName = getCollectionName(modelConf);
        Query query = new Query();
        query.addCriteria(Criteria.where(ModelDAO.CONTEXT_ID_FIELD).is(contextId))
                .addCriteria(Criteria.where(ModelDAO.END_TIME_FIELD).lte(eventTime).gte(oldestAllowedModelTime))
                .with(new Sort(Direction.DESC)).limit(1);

        logger.debug("fetching latest model dao for contextId={} eventTime={} collection={}",contextId,eventTime,collectionName);
		List<ModelDAO> queryResult = mongoTemplate.find(query, ModelDAO.class, collectionName);
		if(CollectionUtils.isEmpty(queryResult))
		{
			return null;
		}
		else
		{
			return queryResult.stream().findFirst().get();
		}
    }
}
