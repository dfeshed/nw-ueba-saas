package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.regex.Pattern;

public class ConstantRegexScorer extends RegexScorer {
	public static final String INVALID_CONSTANT_SCORE_ERROR_MSG = "constantScore must be >= 0 AND <= 100: %d";

	private int constantScore;

	static public void assertConstantScoreValue(int constantScore) {
		Assert.isTrue(constantScore >= 0 && constantScore <= 100, String.format(INVALID_CONSTANT_SCORE_ERROR_MSG, constantScore));
	}

	public ConstantRegexScorer(String scorerName, String featureFieldName, Pattern regexPattern, int constantScore) {
		super(scorerName, featureFieldName, regexPattern);
		assertConstantScoreValue(constantScore);
		this.constantScore = constantScore;
	}

	@Override
	public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
		FeatureScore ret = null;

		if (matches(adeRecordReader)) {
			ret = new FeatureScore(getName(), (double)constantScore);
		}

		return ret;
	}

	public int getConstantScore() {
		return constantScore;
	}
}
