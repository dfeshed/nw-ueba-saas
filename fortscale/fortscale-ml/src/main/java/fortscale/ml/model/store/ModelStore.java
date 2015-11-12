package fortscale.ml.model.store;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

public class ModelStore {
	private static final String COLLECTION_NAME_PREFIX = "model_";

	private MongoTemplate mongoTemplate;
	private MongoDbUtilService mongoDbUtilService;

	@Autowired
	@JsonCreator
	ModelStore(
			@JsonProperty("mongoTemplate") MongoTemplate mongoTemplate,
			@JsonProperty("mongoDbUtilService") MongoDbUtilService mongoDbUtilService) {

		this.mongoTemplate = mongoTemplate;
		this.mongoDbUtilService = mongoDbUtilService;
	}

	public void save(ModelConf modelConf, String contextId, Model model, long sessionId) {
		ModelDAO modelDAO = new ModelDAO(contextId, model, sessionId);
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
