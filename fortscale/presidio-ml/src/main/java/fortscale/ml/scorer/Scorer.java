package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import presidio.ade.domain.record.AdeRecord;

public interface Scorer {
	/**
	 * @return this scorer's name
	 */
	String getName();

	/**
	 * Calculate the score of a specific feature in the given record.
	 *
	 * @param record contains the feature that needs to be scored
	 * @return the final score and a list of all underlying scores
	 */
	FeatureScore calculateScore(AdeRecord record);
}
