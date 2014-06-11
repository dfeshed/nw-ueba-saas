package fortscale.streaming.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class ModelRepositoryImpl implements ModelRepositoryCustom {

	@Autowired
    private MongoTemplate mongoTemplate;
	
	@Override
	public void upsertModel(Model model) {
		// update the model json for the user model name, is not exist insert a new document 
		Query query = new Query();
		query.addCriteria(Criteria.where(Model.MODEL_NAME_FIELD).is(model.getModelName()));
		query.addCriteria(Criteria.where(Model.USER_NAME_FIELD).is(model.getUserName()));
		
		Update update = new Update();
		update.set(Model.JSON_MODEL_FIELD, model.getModelJson());
		
		mongoTemplate.upsert(query, update, Model.class);
	}

}
