package fortscale.ml.model.store;

import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ModelStore {
	private static final String COLLECTION_NAME_PREFIX = "model_";

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
		ModelDAO modelDao = mongoTemplate.findOne(query, ModelDAO.class, collectionName);

		if (modelDao == null) {
			modelDao = new ModelDAO(sessionId, contextId, model, startTime, endTime);
		} else {
			modelDao.setModel(model);
			modelDao.setStartTime(startTime);
			modelDao.setEndTime(endTime);
		}

		mongoTemplate.save(modelDao, collectionName);
	}

	public List<ModelDAO> getModelDaos(ModelConf modelConf, String contextId) {
		String collectionName = getCollectionName(modelConf);

		if (mongoDbUtilService.collectionExists(collectionName)) {
			Query query = new Query();
			query.addCriteria(Criteria.where(ModelDAO.CONTEXT_ID_FIELD).is(contextId));
			return mongoTemplate.find(query, ModelDAO.class, collectionName);
		} else {
			return Collections.emptyList();
		}
	}

	private String getCollectionName(ModelConf modelConf) {
		return String.format("%s%s", COLLECTION_NAME_PREFIX, modelConf.getName());
	}

	private void ensureCollectionExists(String collectionName) {
		if (!mongoDbUtilService.collectionExists(collectionName)) {
			mongoDbUtilService.createCollection(collectionName);
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(ModelDAO.SESSION_ID_FIELD, Direction.ASC));
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(ModelDAO.CONTEXT_ID_FIELD, Direction.ASC));
		}
	}
}
