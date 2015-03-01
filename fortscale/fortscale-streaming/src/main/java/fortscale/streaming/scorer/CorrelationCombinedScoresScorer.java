package fortscale.streaming.scorer;

import org.apache.samza.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fortscale.streaming.ConfigUtils.getConfigPositiveDouble;

public class CorrelationCombinedScoresScorer extends ScorerContainer {
	private static final double MAX_SCORE = 100;

	private double highestScoreWeight;

	public CorrelationCombinedScoresScorer(String name, Config config, ScorerContext context) {
		super(name, config, context);
		highestScoreWeight = getConfigPositiveDouble(config, String.format("fortscale.score.%s.highest.score.weight", name));
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		List<FeatureScore> featureScores = new ArrayList<>();
		List<Double> sortedScores = new ArrayList<>();

		for (Scorer scorer : scorers) {
			FeatureScore featureScore = scorer.calculateScore(eventMessage);
			if (featureScore != null) {
				featureScores.add(featureScore);
				sortedScores.add(featureScore.getScore());
			}
		}

		Collections.sort(sortedScores, Collections.reverseOrder());
		double returnedScore = 0;
		double weight = highestScoreWeight;
		for (double score : sortedScores) {
			returnedScore += score * weight;
			weight = 1 - (returnedScore / MAX_SCORE);
		}

		return new FeatureScore(outputFieldName, returnedScore, featureScores);
	}
}
