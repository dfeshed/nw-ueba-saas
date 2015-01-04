package fortscale.ml.service.dao;

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
			if (existing.getModel().getBarrier().isEventAfterBarrier(model.getModel().getBarrier())) {
				// update existing model if the model given is newer than the saved model barrier
				existing.setModel(model.getModel());
				mongoTemplate.save(existing);
			}
		}
	}

}
