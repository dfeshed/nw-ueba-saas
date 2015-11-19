package fortscale.ml.model.store;

import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.util.Assert;

public class ModelStore {
	private static final String COLLECTION_NAME_PREFIX = "model_";

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;

	public void save(ModelConf modelConf, String contextId, Model model, DateTime sessionStartTime, DateTime sessionEndTime) {
		Assert.notNull(sessionStartTime);
		ModelDAO modelDAO = new ModelDAO(contextId, model, sessionStartTime, sessionEndTime);
		String collectionName = COLLECTION_NAME_PREFIX + modelConf.getName();
		ensureCollectionExists(collectionName);
		mongoTemplate.save(modelDAO, collectionName);
	}

	private void ensureCollectionExists(String collectionName) {
		if (!mongoDbUtilService.collectionExists(collectionName)) {
			mongoDbUtilService.createCollection(collectionName);
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(ModelDAO.CONTEXT_ID_FIELD, Sort.Direction.DESC));
		}
	}
}
