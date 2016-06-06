package fortscale.ml.model.store;

import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.utils.mongodb.FIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ModelStore {
	private static final String COLLECTION_NAME_PREFIX = "model_";
	private static final String ID_FIELD = "_id";

	@Value("${fortscale.model.build.retention.time.in.days}")
	private long retentionTimeInDays;

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;

	public void save(ModelConf modelConf, String sessionId, String contextId, Model model, Date startTime, Date endTime) {
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
		String collectionName = getCollectionName(modelConf);

		Query query = new Query();
		query.addCriteria(Criteria.where(ModelDAO.CONTEXT_ID_FIELD).is(contextId));
		return mongoTemplate.find(query, ModelDAO.class, collectionName);

	}

	public void removeModels(Collection<ModelConf> modelConfs, String sessionId) {
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
		return String.format("%s%s", COLLECTION_NAME_PREFIX, modelConf.getName());
	}

	private void ensureCollectionExists(String collectionName) {
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
}
