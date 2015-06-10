package fortscale.ml.service;

import fortscale.ml.model.prevalance.PrevalanceModel;

public interface ModelService {
	/**
	 * Return true if the model exists in the samza store / repository, false otherwise
	 */
	public boolean modelExists(String context, String modelName) throws Exception;

	/**
	 * Get the model for the user first from the samza store, if not exists look for it in the repository or build a new model
	 */
	public PrevalanceModel getModel(String context, String modelName) throws Exception;

	/**
	 * Export user models to mongodb
	 */
	public void updateModel(String context, PrevalanceModel model) throws Exception;
}
