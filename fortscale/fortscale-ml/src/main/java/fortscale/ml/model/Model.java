package fortscale.ml.model;

public interface Model {
	/**
	 * Scores a given value according to the model.
	 *
	 * @param value the value to score.
	 * @return the score, or null if unable to give a score (e.g. - not enough data was given in build phase).
	 */
	@Deprecated
	Double calculateScore(Object value);
}
