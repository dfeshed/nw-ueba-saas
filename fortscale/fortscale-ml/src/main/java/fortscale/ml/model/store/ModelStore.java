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
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;

public class ModelStore {
	private static final String COLLECTION_NAME_PREFIX = "model_";

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;

	public void save(ModelConf modelConf, String sessionId, String contextId, Model model, Date endTime) {
		String collectionName = COLLECTION_NAME_PREFIX + modelConf.getName();
		ensureCollectionExists(collectionName);

		Query query = new Query();
		query.addCriteria(Criteria.where(ModelDAO.SESSION_ID_FIELD).is(sessionId));
		query.addCriteria(Criteria.where(ModelDAO.CONTEXT_ID_FIELD).is(contextId));

		Update update = new Update();
		update.set(ModelDAO.MODEL_FIELD, model);
		update.set(ModelDAO.END_TIME_FIELD, endTime);

		mongoTemplate.upsert(query, update, collectionName);
	}

	private void ensureCollectionExists(String collectionName) {
		if (!mongoDbUtilService.collectionExists(collectionName)) {
			mongoDbUtilService.createCollection(collectionName);
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(ModelDAO.SESSION_ID_FIELD, Direction.ASC));
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(ModelDAO.CONTEXT_ID_FIELD, Direction.ASC));
		}
	}
}
