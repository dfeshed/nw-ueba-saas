package fortscale.ml.model.store;

import com.mongodb.DBObject;
import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.utils.mongodb.FIndex;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.StreamUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

public class ModelStore {
	private static final String COLLECTION_NAME_PREFIX = "model_";
	private static final String ID_FIELD = "_id";

	@Value("${fortscale.model.build.retention.time.in.days}")
	private long retentionTimeInDays;

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
		ensureCollectionExists(collectionName);
		Query query = new Query();
		query.addCriteria(Criteria.where(ModelDAO.SESSION_ID_FIELD).is(sessionId));
		query.addCriteria(Criteria.where(ModelDAO.CONTEXT_ID_FIELD).is(contextId));
		query.fields().include(ID_FIELD);
		ModelDAO oldModelDao = mongoTemplate.findOne(query, ModelDAO.class, collectionName);
		ModelDAO newModelDao = new ModelDAO(sessionId, contextId, model, startTime, endTime);
		mongoTemplate.insert(newModelDao, collectionName);
		if (oldModelDao != null) mongoTemplate.remove(oldModelDao, collectionName);
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
		String modelGroupName = "model";
		Aggregation agg = newAggregation(
				Aggregation.match(new Criteria(ModelDAO.END_TIME_FIELD).lte(eventEpochtime)),
				Aggregation.sort(new Sort(Direction.DESC, ModelDAO.END_TIME_FIELD)),
				Aggregation.group(ModelDAO.CONTEXT_ID_FIELD).first(ModelDAO.CONTEXT_ID_FIELD).as(modelGroupName)
		);
		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, getCollectionName(modelConf), DBObject.class);
		return StreamUtils.createStreamFromIterator(results.iterator())
				.map(res -> (ModelDAO) res.get(modelGroupName))
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

	private String getCollectionName(ModelConf modelConf) {
		getMetrics().getCollectionName++;
		return String.format("%s%s", COLLECTION_NAME_PREFIX, modelConf.getName());
	}

	private void ensureCollectionExists(String collectionName) {
		getMetrics().ensureCollectionExists++;

		if (!mongoDbUtilService.collectionExists(collectionName)) {
			mongoDbUtilService.createCollection(collectionName);

			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(ModelDAO.SESSION_ID_FIELD, Direction.ASC));

			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(ModelDAO.CONTEXT_ID_FIELD, Direction.ASC));

			mongoTemplate.indexOps(collectionName).ensureIndex(new FIndex()
					.expire(retentionTimeInDays, TimeUnit.DAYS)
					.named(ModelDAO.CREATION_TIME_FIELD)
					.on(ModelDAO.CREATION_TIME_FIELD, Direction.ASC));
		}
	}
	public ModelStoreMetrics getMetrics()
	{
		if (metrics==null)
		{
			metrics = new ModelStoreMetrics(statsService);
		}
		return metrics;
	}
}
