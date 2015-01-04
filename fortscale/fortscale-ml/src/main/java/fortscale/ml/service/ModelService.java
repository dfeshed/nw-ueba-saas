package fortscale.ml.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.dao.Model;
import fortscale.ml.service.dao.ModelRepository;

@Service
public class ModelService {

	private static final Logger logger = LoggerFactory.getLogger(ModelService.class);
	
	@Autowired
	private ModelRepository modelRepository;
	
	
	/** Get the model for the user first from the samza store, if not exists look for it in the repository or build a new model */
	public PrevalanceModel getModelForUser(String username, String modelName) throws Exception {
		// lookup the model in the repository
		Model dto = modelRepository.findByUserNameAndModelName(username, modelName);
		if (dto!=null) 
			return dto.getModel();
		return null;
	}
	
	
	
	
	
	
	/** export user models to mongodb */
	public void updateUserModel(String username, PrevalanceModel model) {
		if (model==null || StringUtils.isEmpty(username)) {
			return;
		}

		Model dto = convertToDTO(username, model, model.getModelName());
		modelRepository.upsertModel(dto);
	}
	
	private Model convertToDTO(String username, PrevalanceModel model, String modelName) {
		Model dto = new Model(modelName, username, model);
		return dto;
	}
	
}
