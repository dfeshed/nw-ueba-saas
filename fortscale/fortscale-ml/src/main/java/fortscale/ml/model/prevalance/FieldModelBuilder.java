package fortscale.ml.model.prevalance;

import org.apache.samza.config.Config;

public interface FieldModelBuilder {
	/**
	 * Initializes the builder right after instantiation.
	 *
	 * @param config          configuration holding the builder's properties.
	 * @param globalModelName global model name in configuration.
	 * @param fieldModelName  field model name in configuration.
	 */
	public void initBuilder(Config config, String globalModelName, String fieldModelName);

	/**
	 * Changes the builder's state according to a given (local) Prevalance Model.
	 *
	 * @param prevalanceModel the given Prevalance Model.
	 */
	public void feedBuilder(PrevalanceModel prevalanceModel);

	/**
	 * Builds a new model according to the current state of the builder.
	 *
	 * @return the new model.
	 */
	public FieldModel buildModel();
}