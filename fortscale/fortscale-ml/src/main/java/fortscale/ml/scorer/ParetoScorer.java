package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.common.event.EventMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParetoScorer extends ScorerContainer {
    private static final double MAX_SCORE = 100;

    private double highestScoreWeight;

    public ParetoScorer(String name, List<Scorer> scorers,  double highestScoreWeight) {
        super(name, scorers);
        this.highestScoreWeight = highestScoreWeight;
    }

    @Override
    public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
        List<FeatureScore> featureScores = new ArrayList<>();
        List<Double> sortedScores = new ArrayList<>();

        for (Scorer scorer : scorers) {
            FeatureScore featureScore = scorer.calculateScore(eventMessage, eventEpochTimeInSec);
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

