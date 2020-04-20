package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParetoScorer extends ScorerContainer {
    private static final double MAX_SCORE = 100;

    private double highestScoreWeight;

    public ParetoScorer(String name, List<Scorer> scorers, double highestScoreWeight) {
        super(name, scorers);
        this.highestScoreWeight = highestScoreWeight;
    }

    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
        List<FeatureScore> featureScores = new ArrayList<>();
        List<Double> sortedScores = new ArrayList<>();

        for (Scorer scorer : scorers) {
            FeatureScore featureScore = scorer.calculateScore(adeRecordReader);
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

        return new FeatureScore(getName(), returnedScore, featureScores);
    }

    public double getHighestScoreWeight() {
        return highestScoreWeight;
    }
}
