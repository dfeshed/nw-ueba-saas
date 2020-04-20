package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import presidio.ade.domain.record.AdeRecordReader;

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
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
        return score == null ? null : new FeatureScore("SimpleTestScorer", score);
    }
}
