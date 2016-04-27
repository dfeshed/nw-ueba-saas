package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.domain.core.FeatureScore;

public class SimpleTestScorer implements Scorer {

    private Double score;
    private String name;

    public SimpleTestScorer(Double score, String name) {
        this.score = score;
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public Double getScore() {
        return score;
    }


    @Override
    public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
        return score == null ? null : new FeatureScore("SimpleTestScorer", score);
    }
}
