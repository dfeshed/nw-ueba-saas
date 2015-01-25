package fortscale.ml.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.ModelService;
import fortscale.ml.service.dao.Model;
import fortscale.ml.service.dao.ModelRepository;

public class ModelServiceImpl implements ModelService{
	
	@Autowired
	private ModelRepository modelRepository;
	
	
	/** Get the model for the user first from the samza store, if not exists look for it in the repository or build a new model */
	@Override
	public PrevalanceModel getModel(String context, String modelName) throws Exception {
		// lookup the model in the repository
		Model dto = modelRepository.findByContextAndModelName(context, modelName);
		if (dto!=null) 
			return dto.getModel();
		return null;
	}
	
	
	
	
	
	
	/** export user models to mongodb */
	@Override
	public void updateModel(String context, PrevalanceModel model) throws Exception{
		if (model==null || StringUtils.isEmpty(context)) {
			return;
		}

		Model dto = convertToDTO(context, model, model.getModelName());
		modelRepository.upsertModel(dto);
	}
	
	private Model convertToDTO(String context, PrevalanceModel model, String modelName) {
		Model dto = new Model(modelName, context, model);
		return dto;
	}
	
}
