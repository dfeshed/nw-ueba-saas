package fortscale.streaming.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ModelRepositoryImpl implements ModelRepositoryCustom {

	@Autowired
    private MongoTemplate mongoTemplate;
	
	@Override
	public void upsertModel(Model model) {
		// update the model json for the user model name, is not exist insert a new document 
		Query query = new Query();
		query.addCriteria(Criteria.where(Model.MODEL_NAME_FIELD).is(model.getModelName()));
		query.addCriteria(Criteria.where(Model.USER_NAME_FIELD).is(model.getUserName()));
		
		Model existing = mongoTemplate.findOne(query, Model.class);
		if (existing==null) {
			mongoTemplate.save(model);
		} else {
			if (existing.getModel().getTimeMark() < model.getModel().getTimeMark()) {
				// update existing model
				existing.setModel(model.getModel());
				mongoTemplate.save(existing);
			}
		}
	}

}
