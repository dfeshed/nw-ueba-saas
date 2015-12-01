package fortscale.ml.model;

public interface Model {
	/**
	 * Scores a given value according to the model.
	 *
	 * @param value the value to score.
	 * @return the score.
	 */
	double calculateScore(Object value);
}
