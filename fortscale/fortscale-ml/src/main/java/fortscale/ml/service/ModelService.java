package fortscale.ml.service;

import fortscale.ml.model.prevalance.PrevalanceModel;

public interface ModelService {
	
	/** Get the model for the user first from the samza store, if not exists look for it in the repository or build a new model */
	public PrevalanceModel getModelForUser(String username, String modelName) throws Exception;

	/** export user models to mongodb */
	public void updateUserModel(String username, PrevalanceModel model) throws Exception;
		
}
