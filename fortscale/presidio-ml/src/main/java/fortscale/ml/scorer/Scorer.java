package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import presidio.ade.domain.record.AdeRecordReader;

public interface Scorer {
	/**
	 * @return this scorer's name
	 */
	String getName();

	/**
	 * Calculate the score of a specific feature provided by the given {@link AdeRecordReader}.
	 *
	 * @param adeRecordReader provides the feature that needs to be scored
	 * @return the final score and a list of all underlying scores
	 */
	FeatureScore calculateScore(AdeRecordReader adeRecordReader);
}
