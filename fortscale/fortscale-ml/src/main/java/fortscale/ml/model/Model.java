package fortscale.ml.model;

public interface Model {
	/**
	 * @return the number of samples from which this model was built.
	 */
	long getNumOfSamples();
}
